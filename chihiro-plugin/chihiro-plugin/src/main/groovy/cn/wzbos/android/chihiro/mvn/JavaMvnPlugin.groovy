package cn.wzbos.android.chihiro.mvn


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment

/**
 * Maven 组件上传(Java Library)
 * Created by wuzongbo on 2020/09/02.
 */
class JavaMvnPlugin implements Plugin<Project> {

    static boolean isReleaseBuild(String ver) {
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
            throw new IllegalStateException('插件仅支持 Java Library!')
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
                    }
                }
            }

            project.signing {
                required { isReleaseBuild(project.PROJ_VERSION) && gradle.taskGraph.hasTask("uploadArchives") }
                sign project.configurations.archives
            }

        }
    }
}
