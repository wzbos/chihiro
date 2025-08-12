package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.utils.Logger
import cn.wzbos.android.chihiro.utils.TextUtils
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.internal.impldep.com.google.gson.annotations.Expose

/**
 * Maven 配置
 * Created by wuzongbo on 2020/09/14.
 */
class MvnConfig {
    static final String CHIHIRO_GROUP_ID = "CHIHIRO_GROUP_ID"
    static final String CHIHIRO_ARTIFACT_ID = "CHIHIRO_ARTIFACT_ID"
    static final String CHIHIRO_VERSION = "CHIHIRO_VERSION"
    static final String CHIHIRO_POM_NAME = "CHIHIRO_POM_NAME"
    static final String CHIHIRO_POM_DESCRIPTION = "CHIHIRO_POM_DESCRIPTION"
    static final String CHIHIRO_POM_URL = "CHIHIRO_POM_URL"
    static final String CHIHIRO_POM_INCEPTION_YEAR = "CHIHIRO_POM_INCEPTION_YEAR"
    static final String CHIHIRO_POM_LICENSE_NAME = "CHIHIRO_POM_LICENSE_NAME"
    static final String CHIHIRO_POM_LICENSE_URL = "CHIHIRO_POM_LICENSE_URL"
    static final String CHIHIRO_POM_DEVELOPER_ID = "CHIHIRO_POM_DEVELOPER_ID"
    static final String CHIHIRO_POM_DEVELOPER_NAME = "CHIHIRO_POM_DEVELOPER_NAME"
    static final String CHIHIRO_POM_DEVELOPER_EMAIL = "CHIHIRO_POM_DEVELOPER_EMAIL"
    static final String CHIHIRO_POM_SCM_CONNECTION = "CHIHIRO_POM_SCM_CONNECTION"
    static final String CHIHIRO_POM_SCM_DEVELOPER_CONNECTION = "CHIHIRO_POM_SCM_DEVELOPER_CONNECTION"
    static final String CHIHIRO_POM_SCM_URL = "CHIHIRO_POM_SCM_URL"
    static final String CHIHIRO_MAVEN_RELEASES_URL = "CHIHIRO_MAVEN_RELEASES_URL"
    static final String CHIHIRO_MAVEN_SNAPSHOTS_URL = "CHIHIRO_MAVEN_SNAPSHOTS_URL"
    static final String CHIHIRO_MAVEN_USERNAME = "CHIHIRO_MAVEN_USERNAME"
    static final String CHIHIRO_MAVEN_PASSWORD = "CHIHIRO_MAVEN_PASSWORD"
    static final String CHIHIRO_MAVEN_LOCAL = "CHIHIRO_MAVEN_LOCAL"

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
    @Expose(serialize = false)
    String mavenUsername
    @Expose(serialize = false)
    String mavenPassword
    boolean mavenLocal = false

    static MvnConfig load(Project project) {
        return new MvnConfig(project)
    }

    static MvnConfig load(Settings settings, String path) throws IOException {
        File file = new File(path)
        if (!file.exists()) {
            throw new FileNotFoundException("配置文件不存在，请添加配置！\nfile:" + file.getPath())
        }
        return new MvnConfig(settings, file)
    }

    MvnConfig(Settings settings, File modulePropertiesFile) {
        File localPropertiesFile = settings.rootDir.toPath().resolve("local.properties").toFile()
        loadProperties(new MultiSourcePropertyReader(null, modulePropertiesFile, localPropertiesFile))
    }

    MvnConfig(Project project) {
        File localPropertiesFile = project.rootProject.file('local.properties')
        loadProperties(new MultiSourcePropertyReader(project, null, localPropertiesFile))
    }

    void loadProperties(MultiSourcePropertyReader reader) {
        groupId = reader.getStringProperty(CHIHIRO_GROUP_ID)
        artifactId = reader.getStringProperty(CHIHIRO_ARTIFACT_ID)
        version = reader.getStringProperty(CHIHIRO_VERSION)
        pomName = reader.getStringProperty(CHIHIRO_POM_NAME)
        pomDescription = reader.getStringProperty(CHIHIRO_POM_DESCRIPTION) ?: ""
        pomUrl = reader.getStringProperty(CHIHIRO_POM_URL) ?: ""
        pomInceptionYear = reader.getStringProperty(CHIHIRO_POM_INCEPTION_YEAR)
        pomLicenseName = reader.getStringProperty(CHIHIRO_POM_LICENSE_NAME)
        pomLicenseUrl = reader.getStringProperty(CHIHIRO_POM_LICENSE_URL)
        pomDeveloperId = reader.getStringProperty(CHIHIRO_POM_DEVELOPER_ID) ?: ""
        pomDeveloperName = reader.getStringProperty(CHIHIRO_POM_DEVELOPER_NAME) ?: ""
        pomDeveloperEMail = reader.getStringProperty(CHIHIRO_POM_DEVELOPER_EMAIL) ?: ""
        pomSCMConnection = reader.getStringProperty(CHIHIRO_POM_SCM_CONNECTION) ?: ""
        pomSCMDeveloperConnection = reader.getStringProperty(CHIHIRO_POM_SCM_DEVELOPER_CONNECTION) ?: ""
        pomSCMUrl = reader.getStringProperty(CHIHIRO_POM_SCM_URL) ?: ""
        mavenLocal = reader.getBooleanProperty(CHIHIRO_MAVEN_LOCAL) ?: false
        mavenReleasesRepoUrl = reader.getStringProperty(CHIHIRO_MAVEN_RELEASES_URL) ?: ""
        mavenSnapshotsRepoUrl = reader.getStringProperty(CHIHIRO_MAVEN_SNAPSHOTS_URL) ?: ""
        mavenUsername = reader.getStringProperty(CHIHIRO_MAVEN_USERNAME) ?: ""
        mavenPassword = reader.getStringProperty(CHIHIRO_MAVEN_PASSWORD) ?: ""
    }

    boolean isReleaseVersion() {
        if (TextUtils.isEmpty(version))
            return false
        return !version.contains("SNAPSHOT")
    }

    boolean hasLicense() {
        return !TextUtils.isEmpty(pomLicenseName) && !TextUtils.isEmpty(pomLicenseUrl)
    }

    boolean isValid() {
        return !TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(artifactId)
    }

    boolean checkConfig() {
        if (TextUtils.isEmpty(groupId)) {
            Logger.e("缺少 ${CHIHIRO_GROUP_ID} 配置。")
            return false
        }

        if (TextUtils.isEmpty(artifactId)) {
            Logger.e("缺少 ${CHIHIRO_ARTIFACT_ID} 配置。")
            return false
        }

        if (TextUtils.isEmpty(version)) {
            Logger.e("缺少 ${CHIHIRO_VERSION} 配置。")
            return false
        }

        if (!mavenLocal) {
            if (isReleaseVersion()) {
                if (TextUtils.isEmpty(mavenReleasesRepoUrl)) {
                    Logger.e("缺少 ${CHIHIRO_MAVEN_RELEASES_URL} 配置。")
                    return false
                }
            } else {
                if (TextUtils.isEmpty(mavenSnapshotsRepoUrl)) {
                    Logger.e("缺少 ${CHIHIRO_MAVEN_SNAPSHOTS_URL} 配置。")
                    return false
                }
            }

            if (TextUtils.isEmpty(mavenUsername)) {
                Logger.e("缺少 ${CHIHIRO_MAVEN_USERNAME} 配置。")
                return false
            }
            if (TextUtils.isEmpty(mavenPassword)) {
                Logger.e("缺少 ${CHIHIRO_MAVEN_PASSWORD} 配置。")
                return false
            }
        }

        return true
    }

    @Override
    String toString() {
        return "MvnConfig{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", pomName='" + pomName + '\'' +
                ", pomDescription='" + pomDescription + '\'' +
                ", pomUrl='" + pomUrl + '\'' +
                ", pomInceptionYear='" + pomInceptionYear + '\'' +
                ", pomLicenseName='" + pomLicenseName + '\'' +
                ", pomLicenseUrl='" + pomLicenseUrl + '\'' +
                ", pomDeveloperId='" + pomDeveloperId + '\'' +
                ", pomDeveloperName='" + pomDeveloperName + '\'' +
                ", pomDeveloperEMail='" + pomDeveloperEMail + '\'' +
                ", pomSCMConnection='" + pomSCMConnection + '\'' +
                ", pomSCMDeveloperConnection='" + pomSCMDeveloperConnection + '\'' +
                ", pomSCMUrl='" + pomSCMUrl + '\'' +
                ", mavenReleasesRepoUrl='" + mavenReleasesRepoUrl + '\'' +
                ", mavenSnapshotsRepoUrl='" + mavenSnapshotsRepoUrl + '\'' +
                ", mavenUsername='" + mavenUsername + '\'' +
                ", mavenPassword='" + mavenPassword + '\'' +
                ", mavenLocal=" + mavenLocal +
                '}';
    }
}
