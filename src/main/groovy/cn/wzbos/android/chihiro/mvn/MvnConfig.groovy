package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.utils.TextUtils
import org.gradle.api.Project

/**
 * Maven 配置
 * Created by wuzongbo on 2020/09/14.
 */
class MvnConfig {
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
        loadProperties(new MultiSourcePropertyReader(null, properties, localProperties))
    }

    MvnConfig(Project project) {
        def localProperties = null
        File file = project.rootProject.file('local.properties')
        println("localProperties:localProperties")
        if (file.exists()) {
            localProperties = new Properties()
            try (FileInputStream inputStream = new FileInputStream(file)) {
                localProperties.load(inputStream)
            }
        }

        loadProperties(new MultiSourcePropertyReader(project, null, localProperties))
    }


    void loadProperties(MultiSourcePropertyReader reader) {
        groupId = reader.getStringProperty("GROUP_ID", "PROJ_GROUP")
        artifactId = reader.getStringProperty("ARTIFACT_ID", "PROJ_ARTIFACTID")
        version = reader.getStringProperty("VERSION", "PROJ_VERSION")
        pomName = reader.getStringProperty("POM_NAME", "PROJ_NAME")
        pomDescription = reader.getStringProperty("POM_DESCRIPTION", "PROJ_DESCRIPTION") ?: ""
        pomUrl = reader.getStringProperty("POM_URL", "PROJ_WEBSITEURL") ?: ""
        pomInceptionYear = reader.getStringProperty("POM_INCEPTION_YEAR")
        pomLicenseName = reader.getStringProperty("POM_LICENSE_NAME", "LICENSE_NAME")
        pomLicenseUrl = reader.getStringProperty("POM_LICENSE_URL", "LICENSE_URL")
        pomDeveloperId = reader.getStringProperty("POM_DEVELOPER_ID", "DEVELOPER_ID") ?: ""
        pomDeveloperName = reader.getStringProperty("POM_DEVELOPER_NAME", "DEVELOPER_NAME") ?: ""
        pomDeveloperEMail = reader.getStringProperty("POM_DEVELOPER_EMAIL", "DEVELOPER_EMAIL") ?: ""
        pomSCMConnection = reader.getStringProperty("POM_SCM_CONNECTION") ?: ""
        pomSCMDeveloperConnection = reader.getStringProperty("POM_SCM_DEVELOPER_CONNECTION") ?: ""
        pomSCMUrl = reader.getStringProperty("POM_SCM_URL") ?: ""
        enableJReleaser = reader.getBooleanProperty("ENABLE_JRELEASER") ?: ""
        mavenReleasesRepoUrl = reader.getStringProperty("MAVEN_RELEASE_URL", "MAVEN_RELEASES_URL") ?: ""
        mavenSnapshotsRepoUrl = reader.getStringProperty("MAVEN_SNAPSHOTS_URL") ?: ""
        mavenUsername = reader.getStringProperty("MAVEN_USERNAME") ?: ""
        mavenPassword = reader.getStringProperty("MAVEN_PASSWORD", "MAVEN_PWD") ?: ""
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