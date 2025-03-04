package cn.wzbos.android.chihiro.mvn

import org.gradle.api.Project
import cn.wzbos.android.chihiro.utils.Logger
import cn.wzbos.android.chihiro.utils.TextUtils

/**
 * Maven 配置
 * Created by wuzongbo on 2020/09/14.
 */
class MvnConfig {
    final static String GRADLE_FILE_NAME = "gradle.properties"
    String groupId
    String artifactId
    String version
    String pomName
    String pomDescription
    String pomUrl
    String pomInceptionYear
    String pomLicenseName
    String pomLicenseUrl
    String pomDeveloperId
    String pomDeveloperName
    String pomDeveloperEMail
    String pomSCMConnection
    String pomSCMDeveloperConnection
    String pomSCMUrl
    String mavenReleasesRepoUrl
    String mavenSnapshotsRepoUrl
    String mavenUsername
    String mavenPassword
    boolean enableJReleaser = false

    static MvnConfig load(Project project) {
        return new MvnConfig(project)
    }

    static MvnConfig load(String path) throws IOException {
        File file = new File(path)
        if (!file.exists()) {
            throw new FileNotFoundException("配置文件不存在，请添加配置！\nfile:" + file.getPath())
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Properties properties = new Properties()
            properties.load(inputStream)
            return new MvnConfig(properties)
        }
    }

    MvnConfig(Properties properties) {
        loadProperties(null, properties)
    }

    MvnConfig(Project project) {
        loadProperties(project, null)
    }

    static String getProperty(Properties properties, Project project, String... keys) {
        for (String key : keys) {
            if (project != null && project.hasProperty(key)) {
                return String.valueOf(project.findProperty(key))
            } else if (properties != null && properties.containsKey(key)) {
                return properties.getProperty(key)
            }
        }
        return null
    }

    static boolean getBooleanProperty(Properties properties, Project project, String... keys) {
        for (String key : keys) {
            if (project != null && project.hasProperty(key)) {
                return Boolean.parseBoolean(String.valueOf(project.findProperty(key)))
            } else if (properties != null && properties.containsKey(key)) {
                return Boolean.parseBoolean(properties.getProperty(key))
            }
        }
        return null
    }

    void loadProperties(Project project, Properties properties) {
        groupId = getProperty(properties, project, "GROUP_ID", "PROJ_GROUP")
        artifactId = getProperty(properties, project, "ARTIFACT_ID", "PROJ_ARTIFACTID")
        version = getProperty(properties, project, "VERSION", "PROJ_VERSION")
        pomName = getProperty(properties, project, "POM_NAME", "PROJ_NAME")
        pomDescription = getProperty(properties, project, "POM_DESCRIPTION", "PROJ_DESCRIPTION") ?: ""
        pomUrl = getProperty(properties, project, "POM_URL", "PROJ_WEBSITEURL") ?: ""
        pomInceptionYear = getProperty(properties, project, "POM_INCEPTION_YEAR")
        pomLicenseName = getProperty(properties, project, "POM_LICENSE_NAME", "LICENSE_NAME")
        pomLicenseUrl = getProperty(properties, project, "POM_LICENSE_URL", "LICENSE_URL")
        pomDeveloperId = getProperty(properties, project, "POM_DEVELOPER_ID", "DEVELOPER_ID") ?: ""
        pomDeveloperName = getProperty(properties, project, "POM_DEVELOPER_NAME", "DEVELOPER_NAME") ?: ""
        pomDeveloperEMail = getProperty(properties, project, "POM_DEVELOPER_EMAIL", "DEVELOPER_EMAIL") ?: ""
        pomSCMConnection = getProperty(properties, project, "POM_SCM_CONNECTION") ?: ""
        pomSCMDeveloperConnection = getProperty(properties, project, "POM_SCM_DEVELOPER_CONNECTION") ?: ""
        pomSCMUrl = getProperty(properties, project, "POM_SCM_URL") ?: ""
        enableJReleaser = getBooleanProperty(properties, project, "ENABLE_JRELEASER") ?: ""
        mavenReleasesRepoUrl = getProperty(properties, project, "MAVEN_RELEASE_URL", "MAVEN_RELEASES_URL") ?: ""
        mavenSnapshotsRepoUrl = getProperty(properties, project, "MAVEN_SNAPSHOTS_URL") ?: ""
        mavenUsername = getProperty(properties, project, "MAVEN_USERNAME") ?: ""
        mavenPassword = getProperty(properties, project, "MAVEN_PASSWORD", "MAVEN_PWD") ?: ""
    }


    boolean hasLicense() {
        return !TextUtils.isEmpty(pomLicenseName) && !TextUtils.isEmpty(pomLicenseUrl)
    }

    boolean isValid() {
        return groupId != null && !groupId.isEmpty() && artifactId != null && !artifactId.isEmpty()
    }


    @Override
    String toString() {
        return "MvnConfig{" +
                "enableJReleaser=" + enableJReleaser +
                ", mavenSnapshotsRepoUrl='" + mavenSnapshotsRepoUrl + '\'' +
                ", mavenReleasesRepoUrl='" + mavenReleasesRepoUrl + '\'' +
                ", pomSCMUrl='" + pomSCMUrl + '\'' +
                ", pomSCMDeveloperConnection='" + pomSCMDeveloperConnection + '\'' +
                ", pomSCMConnection='" + pomSCMConnection + '\'' +
                ", pomDeveloperEMail='" + pomDeveloperEMail + '\'' +
                ", pomDeveloperName='" + pomDeveloperName + '\'' +
                ", pomDeveloperId='" + pomDeveloperId + '\'' +
                ", pomLicenseUrl='" + pomLicenseUrl + '\'' +
                ", pomLicenseName='" + pomLicenseName + '\'' +
                ", pomInceptionYear='" + pomInceptionYear + '\'' +
                ", pomUrl='" + pomUrl + '\'' +
                ", pomDescription='" + pomDescription + '\'' +
                ", pomName='" + pomName + '\'' +
                ", version='" + version + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }

}