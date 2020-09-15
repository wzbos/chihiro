package cn.wzbos.android.chihiro.settings

/**
 * Chihiro Settings
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroSettings {
    final static String GRADLE_NAME = "chihiro.gradle"

    boolean log = false
    /**
     * 千寻插件工程配置集合
     */
    List<ChihiroProject> projects
    /**
     * true：开启Maven上传功能，false关闭maven上传功能
     */
    boolean maven = false
    /**
     * 企业微信机器人key
     */
    String wechat_key

    boolean isDebug(String projectName) {
        if (projects == null)
            return false

        for (ChihiroProject project : projects) {
            if (project.name.equalsIgnoreCase(projectName)) {
                return project.debug
            }
        }
        return false
    }

    String getLocalProjectName(String rootProjectName, String group, String module) {
        if (projects == null)
            return null

        for (ChihiroProject project : projects) {
            if (!project.debug)
                return null

            if (project.modules == null)
                return null

            for (ChihiroModule m : project.modules) {
                if (m.group == group) {
                    if (m.artifactId == module) {
                        return project.name
                    }
                }
            }
        }
        return null
    }


    @Override
    String toString() {
        return "ChihiroSettings{" +
                "log=" + log +
                ", projects=" + projects +
                '}'
    }
}