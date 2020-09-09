package cn.wzbos.android.chihiro.mvn

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Maven 组件上传(Android Library)
 * Created by wuzongbo on 2020/09/02.
 */
class AndroidMvnPlugin implements Plugin<Project> {

    private static boolean isReleaseBuild(String ver) {
        return !ver.contains("SNAPSHOT")
    }

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin('com.android.library')) {
            throw new IllegalStateException('插件仅支持 Android Library!')
        }

        project.apply plugin: 'maven'
        project.apply plugin: 'signing'

        project.afterEvaluate {
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        beforeDeployment { MavenDeployment deployment -> project.signing.signPom(deployment) }

                        pom.groupId = project.PROJ_GROUP
                        pom.artifactId = project.PROJ_ARTIFACTID
                        pom.version = project.PROJ_VERSION

                        if (!isReleaseBuild(project.PROJ_VERSION)) {
                            snapshotRepository(url: project.MAVEN_SNAPSHOTS_URL) {
                                authentication(userName: project.MAVEN_USERNAME, password: project.MAVEN_PWD)
                            }
                        } else {
                            repository(url: project.MAVEN_RELEASE_URL) {
                                authentication(userName: project.MAVEN_USERNAME, password: project.MAVEN_PWD)
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
                                        project.getByName("")
                                        File gradlePropertiesFile = new File("${project.projectDir.parent}/$dep.artifactId/gradle.properties")
                                        println(">>>> dep:$dep")
                                        println(">>>> file:${gradlePropertiesFile.path}")
                                        if (!gradlePropertiesFile.exists()) {
                                            throw new FileNotFoundException("Maven配置文件不存在，请添加配置！\nfile:${gradlePropertiesFile.path}")
                                        }
                                        Properties properties = new Properties()
                                        properties.load(new FileInputStream(gradlePropertiesFile))
                                        def mGroup = properties.getProperty("PROJ_GROUP")
                                        def mVersion = properties.getProperty("PROJ_VERSION")
                                        println("dependencies => $mGroup:$dep.artifactId:$mVersion")
                                        dep.setGroupId(mGroup)
                                        dep.setVersion(mVersion)
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

            //生成文档注释

            project.task(type: Javadoc, "androidJavadocs") {
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
//        classpath += files(ext.androidJar)
                classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
//        classpath += configurations.javadocDeps
            }

            //将文档打包成jar
            project.task([type: Jar, dependsOn: project.getTasksByName("androidJavadocs", true)], "androidJavadocsJar") {
                classifier = 'javadoc'
                from project.androidJavadocs.destinationDir
            }

            //将源码打包
            project.task(type: Jar, "androidSourcesJar") {
                classifier = 'sources'
                from project.android.sourceSets.main.java.srcDirs
            }

            project.artifacts {
                archives project.androidSourcesJar
                archives project.androidJavadocsJar
            }

            project.task("javaDocBuild") {
                // 生成对应的Sources类
                project.androidSourcesJar
                project.androidJavadocsJar
            }
        }
    }
}
