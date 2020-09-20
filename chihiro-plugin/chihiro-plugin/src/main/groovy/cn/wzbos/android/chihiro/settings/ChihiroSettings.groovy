package cn.wzbos.android.chihiro.settings


import cn.wzbos.android.chihiro.mvn.MvnConfig
import cn.wzbos.android.chihiro.utils.Logger
import org.gradle.api.initialization.Settings

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
    Map<String, ChihiroProject> projects = new LinkedHashMap<String, ChihiroProject>()
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

        ChihiroProject project = projects.get(projectName);

        if (project == null)
            return false

        return project.debug
    }

    String getLocalProjectName(String group, String module) {
        if (projects == null)
            return null

        for (Map.Entry<String, ChihiroProject> kv : projects.entrySet()) {
            String projectName = kv.key
            ChihiroProject project = kv.value

            if (!project.debug)
                return null

            if (project.modules == null)
                return null

            for (MvnConfig m : project.modules) {
                if (m.group == group) {
                    if (m.artifactId == module) {
                        return projectName
                    }
                }
            }
        }
        return null
    }

    static def get = { plugin ->
        if (plugin.gradle.ext.has("chihiroSettings")) {
            return plugin.gradle.ext.chihiroSettings
        }

        ChihiroSettings chihiroSettings = plugin.extensions.create('chihiro', ChihiroSettings)
        def debugGradleFile = plugin.rootDir.path + File.separator + GRADLE_NAME
        if (new File(debugGradleFile).exists()) {
            plugin.apply from: debugGradleFile
        }

        Logger.d("$chihiroSettings")
        return chihiroSettings
    }

    void save(Settings settings) {
        settings.gradle.ext.chihiroSettings = this
        Logger.d("$this")
    }


    @Override
    String toString() {
        return "ChihiroSettings{" +
                "log=" + log +
                ", projects=" + projects +
                '}'
    }
}