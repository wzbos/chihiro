package cn.wzbos.android.chihiro.utils

import org.gradle.api.Project
import org.gradle.process.ExecSpec

/**
 * Web Hook工具类
 * Created by wuzongbo on 2020/09/11.
 */
class WebHookUtils {

    /**
     * 发送通知到企业微信
     * @param project Project
     * @param key 微信机器人key
     * @param content 发送内容，Markdown格式
     */
    static void sendToWeChat(Project project, String key, String content) {
        def cmd = 'curl -s "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=' + key + '"' +
                ' -H "Content-Type: application/json"' +
                ' -d \'{"msgtype": "markdown","markdown": {"content":"' + content + '"}}\''

        project.exec {
            ExecSpec execSpec ->
                executable 'bash'
                args '-c', cmd
        }
        println("\n")
    }
}
