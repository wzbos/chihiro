package cn.wzbos.android.chihiro.settings


import cn.wzbos.android.chihiro.mvn.MvnConfig
import cn.wzbos.android.chihiro.trigger.DingTalk
import cn.wzbos.android.chihiro.trigger.TriggerRequest
import cn.wzbos.android.chihiro.trigger.WeChat
import cn.wzbos.android.chihiro.trigger.WebHook
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

    WeChat wechat

    DingTalk dingtalk

    /**
     * 其他
     */
    WebHook webhook

    void webhook(Closure closure) {
        webhook = new WebHook(closure)
    }

    /**
     * 企业微信机器人key
     */
    @Deprecated
    void wechat_key(String key) {
        wechat = new WeChat("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=$key")
    }

    /**
     * 企业微信机器人
     */
    void wechat(Closure closure) {
        wechat = new WeChat(closure)
    }

    /**
     * 钉钉机器人
     */
    void dingtalk(Closure closure) {
        dingtalk = new DingTalk(closure)
    }

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
                continue

            if (project.modules != null) {
                for (MvnConfig m : project.modules) {
                    if (m.groupId == group && m.artifactId == module) {
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
        Logger.isDebug = chihiroSettings.log
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

    int send(String projectName, List<String> archives) {
        TriggerRequest request = new TriggerRequest(projectName, archives)
        int code = 0
        if (wechat != null) {
            code = wechat.send(request)
        }
        if (dingtalk != null) {
            code = dingtalk.send(request)
        }
        if (webhook != null) {
            code = webhook.send(request)
        }
        return code
    }
}