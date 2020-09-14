package cn.wzbos.android.chihiro.mvn

import org.gradle.api.Project

/**
 * Maven 配置
 * Created by wuzongbo on 2020/09/14.
 */
class MvnConfig {
    final static String GRADLE_FILE_NAME = "gradle.properties"
    //组件名
    String name
    //组件所属组
    String group
    //组件ID
    String artifactId
    //组件版本名称
    String version
    //组件版本号
    String versionCode
    //工程名称
    String POMName
    //组件说明
    String description
    //组件发布类型，aar或者jar
    String packaging
    //组件gitlab仓库地址
    String WebsiteUrl
    //issues 地址
    String issueTrackerUrl
    //Git仓库地址
    String vcsUrl
    //开发者ID
    String developerId
    //开发者名字
    String developerName
    //开发者邮箱
    String developerEmail

    static MvnConfig load(Project project) {
        return load("${project.projectDir.path}/${GRADLE_FILE_NAME}")
    }

    static MvnConfig load(Project dependenceProject, String artifactId) {
        return load("${dependenceProject.projectDir.parent}/$artifactId/${GRADLE_FILE_NAME}")
    }

    static MvnConfig load(String path) {
        return load(new File(path))
    }

    static MvnConfig load(File file) {
        if (!file.exists()) {
            throw new FileNotFoundException("配置文件不存在，请添加配置！\nfile:${file.path}")
        }
        FileInputStream inputStream
        try {
            inputStream = new FileInputStream(file)
            return new MvnConfig(inputStream)
        } catch (Exception e) {
            e.printStackTrace()
            return null
        } finally {
            if (inputStream != null)
                inputStream.close()
        }
    }

    private MvnConfig(FileInputStream inputStream) {
        Properties properties = new Properties()
        properties.load(inputStream)
        name = properties.getProperty("PROJ_NAME")
        group = properties.getProperty("PROJ_GROUP")
        artifactId = properties.getProperty("PROJ_ARTIFACTID")
        version = properties.getProperty("PROJ_VERSION")
        versionCode = properties.getProperty("PROJ_VERSION_CODE")
        POMName = properties.getProperty("PROJ_POM_NAME")
        description = properties.getProperty("PROJ_DESCRIPTION")
        packaging = properties.getProperty("POM_PACKAGING")
        WebsiteUrl = properties.getProperty("PROJ_WEBSITEURL")
        issueTrackerUrl = properties.getProperty("PROJ_ISSUETRACKERURL")
        vcsUrl = properties.getProperty("PROJ_VCSURL")
        developerId = properties.getProperty("DEVELOPER_ID")
        developerName = properties.getProperty("DEVELOPER_NAME")
        developerEmail = properties.getProperty("DEVELOPER_EMAIL")
    }
}