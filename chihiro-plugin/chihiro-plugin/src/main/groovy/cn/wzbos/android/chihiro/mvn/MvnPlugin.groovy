package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.settings.ChihiroSettings
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Maven 组件上传(Android Library)
 * Created by wuzongbo on 2020/09/02.
 */
class MvnPlugin implements Plugin<Project> {
    static PublishListener uploadArchivesListener = null

    private static boolean isReleaseBuild(String ver) {
        return !ver.contains("SNAPSHOT")
    }

    private static String getUrl(Project project, String url) {
        if (url.toLowerCase().startsWith("http")) {
            return url
        } else {
            return project.uri(url).toString()
        }
    }

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin('com.android.library') && !project.plugins.hasPlugin('java')) {
            throw new IllegalStateException('插件仅支持 Android Library 与 Java Library!')
        }

        project.apply plugin: 'maven'
        project.apply plugin: 'signing'

        ChihiroSettings chihiroSettings = project.extensions.getByName('chihiro')

        if (uploadArchivesListener == null) {
            uploadArchivesListener = new PublishListener(chihiroSettings)
        } else {
            project.gradle.removeListener(uploadArchivesListener)
        }
        project.gradle.addListener(uploadArchivesListener)


        project.afterEvaluate {
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        beforeDeployment { MavenDeployment deployment -> project.signing.signPom(deployment) }

                        pom.groupId = project.PROJ_GROUP
                        pom.artifactId = project.PROJ_ARTIFACTID
                        pom.version = project.PROJ_VERSION

                        if (!isReleaseBuild(project.PROJ_VERSION)) {
                            snapshotRepository(url: getUrl(project, project.MAVEN_SNAPSHOTS_URL)) {
                                if (project.hasProperty("MAVEN_USERNAME") && project.hasProperty("MAVEN_PWD")) {
                                    authentication(userName: project.MAVEN_USERNAME, password: project.MAVEN_PWD)
                                }
                            }
                        } else {
                            repository(url: getUrl(project, project.MAVEN_RELEASE_URL)) {
                                if (project.hasProperty("MAVEN_USERNAME") && project.hasProperty("MAVEN_PWD")) {
                                    authentication(userName: project.MAVEN_USERNAME, password: project.MAVEN_PWD)
                                }
                            }
                        }

                        pom.project {
                            name project.PROJ_POM_NAME
                            packaging project.POM_PACKAGING
                            description project.PROJ_DESCRIPTION
                            url project.PROJ_WEBSITEURL

                            scm {
                                url project.PROJ_VCSURL
                                connection project.DEVELOPER_EMAIL
                                developerConnection project.DEVELOPER_EMAIL
                            }

                            developers {
                                developer {
                                    id project.DEVELOPER_ID
                                    name project.DEVELOPER_NAME
                                }
                            }
                        }

                        pom.whenConfigured { pom ->
                            pom.dependencies.forEach { dep ->
                                if ("unspecified".equalsIgnoreCase(dep.getVersion())) {
                                    try {
                                        MvnConfig mvnConfig = MvnConfig.load(project, dep.artifactId)
                                        println("[Chihiro] dependencies => ${mvnConfig.group}:$mvnConfig.artifactId:$mvnConfig.version")
                                        dep.setGroupId(mvnConfig.group)
                                        dep.setVersion(mvnConfig.version)
                                    } catch (Exception e) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }

            }

            project.signing {
                required { isReleaseBuild(project.PROJ_VERSION) && gradle.taskGraph.hasTask("uploadArchives") }
                sign project.configurations.archives
            }


            if (project.hasProperty("android")) {
                //将源码打包
                project.task(type: Jar, "sourcesJar") {
                    classifier = 'sources'
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
                    classifier = 'sources'
                    from project.sourceSets.main.allSource
                }
            }

            //将文档打包成jar
            project.task([type: Jar, dependsOn: project.getTasksByName("javadoc", true)], "javadocJar") {
                classifier = 'javadoc'
                from project.javadoc.destinationDir
            }

            project.artifacts {
                archives project.sourcesJar
                archives project.javadocJar
            }

            project.task("javaDocBuild") {
                project.sourcesJar
                project.javadocJar
            }
        }
    }

}
