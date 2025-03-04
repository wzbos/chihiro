package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.utils.Logger
import cn.wzbos.android.chihiro.utils.TextUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningPlugin

/**
 * Maven 组件上传(Android Library)
 * <A href="https://docs.gradle.org/current/userguide/publishing_maven.html">Maven Publish</A>
 * <a
 * Created by wuzongbo on 2020/09/02.
 */
class MvnPlugin implements Plugin<Project> {
    static MvnListener uploadArchivesListener = null

    private static boolean isReleaseBuild(String ver) {
        return !ver.contains("SNAPSHOT")
    }

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin('com.android.library') && !project.plugins.hasPlugin('java')) {
            throw new IllegalStateException('插件仅支持 Android Library 与 Java Library!')
        }

        project.apply plugin: MavenPublishPlugin
        project.apply plugin: SigningPlugin

        if (uploadArchivesListener == null) {
            uploadArchivesListener = new MvnListener()
        } else {
            project.gradle.removeListener(uploadArchivesListener)
        }
        project.gradle.addListener(uploadArchivesListener)

        project.beforeEvaluate {
            project.android.publishing {
                singleVariant("release") {
                    withSourcesJar()
                    withJavadocJar()
                }
                singleVariant("debug") {
                    withSourcesJar()
                    withJavadocJar()
                }
                multipleVariants {
                    withSourcesJar()
                    withJavadocJar()
                    allVariants()
                }
            }
        }

        project.afterEvaluate {
            MvnConfig mvnConfig = MvnConfig.load(project)
            def buildType = project.findProperty("targetComponent") ?: "release"
            Logger.i("BuildType: $buildType")
            project.publishing {
                repositories {
                    maven {
                        if (mvnConfig.enableJReleaser) {
                            url = project.layout.buildDirectory.dir('staging-deploy')
                        } else {
                            allowInsecureProtocol = true
                            url = isReleaseBuild(proVersion) ? mvnConfig.mavenReleasesRepoUrl : mvnConfig.mavenSnapshotsRepoUrl
                            credentials {
                                username = mvnConfig.mavenUsername
                                password = mvnConfig.mavenPassword
                            }
                        }
                    }
                }

                project.components.each { component ->
                    println("components: ${component.name}")
                    if (component.name != buildType) return

                    def appendage = component.name.replaceAll("[Rr]elease", "")
                    if (!appendage.isEmpty())
                        appendage = "-$appendage"

                    println("appendage: $appendage")

                    publications {
                        "${component.name}"(MavenPublication) {
                            groupId = mvnConfig.groupId
                            artifactId = mvnConfig.artifactId + appendage
                            version = mvnConfig.version
                            from component
                            pom {
                                name = mvnConfig.pomName
                                description = mvnConfig.pomDescription
                                url = mvnConfig.pomUrl
                                if (!TextUtils.isEmpty(mvnConfig.pomInceptionYear)) {
                                    inceptionYear = mvnConfig.pomInceptionYear
                                }

                                if (!mvnConfig.hasLicense()) {
                                    licenses {
                                        license {
                                            name = mvnConfig.pomLicenseName
                                            url = mvnConfig.pomLicenseUrl
                                        }
                                    }
                                }
                                developers {
                                    developer {
                                        id = mvnConfig.pomDeveloperId
                                        name = mvnConfig.pomDeveloperName
                                        email = mvnConfig.pomDeveloperEMail
                                    }
                                }
                                scm {
                                    connection = mvnConfig.pomSCMConnection
                                    developerConnection = mvnConfig.pomSCMDeveloperConnection
                                    url = mvnConfig.pomSCMUrl
                                }
                            }
                        }
                    }
                }
            }

            project.extensions.configure("signing") { t ->
                t.required { isReleaseBuild(mvnConfig.version) && project.gradle.taskGraph.hasTask("publish") }
                project.publishing.publications.each { pub ->
                    t.sign(pub)
                }
            }
        }
    }
}

