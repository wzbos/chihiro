package cn.wzbos.android.chihiro

import org.gradle.api.Project
import org.gradle.process.ExecSpec

/**
 * 微信机器人通知
 * Created by wuzongbo on 2020/09/11.
 */
class WeChat {
    /**
     * 发送通知
     * @param project Project
     * @param key 微信机器人key
     * @param content 发送内容，Markdown格式
     */
    static void sendNotify(Project project, String key, String content) {

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
