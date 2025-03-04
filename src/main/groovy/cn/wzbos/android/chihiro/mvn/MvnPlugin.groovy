package cn.wzbos.android.chihiro.mvn

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningPlugin

/**
 * Maven 组件上传(Android Library)
 * <A href="https://docs.gradle.org/current/userguide/publishing_maven.html">Maven Publish</A>
 * < a
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
            def buildType = project.hasProperty("targetComponent") ? project.targetComponent : "release"
            println("buildType: $buildType")
            project.publishing {

                repositories {
                    maven {
                        def releasesRepoUrl = project.MAVEN_RELEASE_URL
                        def snapshotsRepoUrl = project.MAVEN_SNAPSHOTS_URL
                        allowInsecureProtocol = true
                        url = isReleaseBuild(project.PROJ_VERSION) ? releasesRepoUrl : snapshotsRepoUrl
                        credentials {
                            username = project.MAVEN_USERNAME
                            password = project.MAVEN_PWD
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
                            groupId = project.PROJ_GROUP
                            artifactId = project.PROJ_ARTIFACTID + appendage
                            version = project.PROJ_VERSION
                            from component
                            pom {
                                name = project.PROJ_POM_NAME
                                description = project.PROJ_DESCRIPTION
                                url = project.PROJ_WEBSITEURL
                                if (hasProperty("LICENSE_NAME") && hasProperty("LICENSE_URL")) {
                                    licenses {
                                        license {
                                            name = project.LICENSE_NAME
                                            url = project.LICENSE_URL
                                        }
                                    }
                                }
                                developers {
                                    developer {
                                        id = project.DEVELOPER_ID
                                        name = project.DEVELOPER_NAME
                                        email = project.DEVELOPER_EMAIL
                                    }
                                }
                                scm {
                                    connection = project.DEVELOPER_EMAIL
                                    developerConnection = project.DEVELOPER_EMAIL
                                    url = project.PROJ_VCSURL
                                }
                            }
                        }
                    }
                }
            }

            if (buildType == "release") {
                project.extensions.configure("signing", { t ->
                    t.required { isReleaseBuild(project.PROJ_VERSION) && project.gradle.taskGraph.hasTask("publish") }
                    t.sign project.publishing.publications.release
                })
            }
        }
    }
}

