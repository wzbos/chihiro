package cn.wzbos.android.chihiro.mvn


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
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


        project.afterEvaluate {

            if (project.hasProperty("android")) {
                //将源码打包
                project.task(type: Jar, "sourcesJar") {
                    archiveClassifier.set("sources")
                    from project.android.sourceSets.main.java.srcDirs
                }

                //生成文档注释
                project.task(type: Javadoc, "javadoc") {
                    failOnError = false
                    source = project.android.sourceSets.main.java.srcDirs
                    ext.androidJar = "${project.android.sdkDirectory}/platforms/${project.android.compileSdkVersion}/android.jar"
                    options {
                        encoding 'utf-8'
                        charSet 'utf-8'
                        links 'http://docs.oracle.com/javase/7/docs/api/'
                        linksOffline "https://developer.android.com/reference", "${project.android.sdkDirectory}/docs/reference"
                    }
                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                    options.encoding = 'utf-8'
                    classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
                }

            } else {
                // Java libraries
                project.task(type: Jar, dependsOn: project.getTasksByName("classes", true), "sourcesJar") {
                    archiveClassifier.set("sources")
                    from project.sourceSets.main.allSource
                }
            }

            //将文档打包成jar
            project.task([type: Jar, dependsOn: project.getTasksByName("javadoc", true)], "javadocJar") {
                from project.javadoc.destinationDir
                archiveClassifier.set("javadoc")
            }

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

                publications {
                    release(MavenPublication) {
                        groupId = project.PROJ_GROUP
                        artifactId = project.PROJ_ARTIFACTID
                        version = project.PROJ_VERSION
                        from project.components.release
                        artifact project.sourcesJar
                        artifact project.javadocJar
                        pom {
                            name = project.PROJ_POM_NAME
                            description = project.PROJ_DESCRIPTION
                            url = project.PROJ_WEBSITEURL
                            licenses {
                                license {
                                    name = project.LICENSE_NAME
                                    url = project.LICENSE_URL
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

            project.extensions.configure("signing", { t ->
                t.required { isReleaseBuild(project.PROJ_VERSION) && project.gradle.taskGraph.hasTask("publish") }
                t.sign project.publishing.publications.release
            })
        }
    }

}

