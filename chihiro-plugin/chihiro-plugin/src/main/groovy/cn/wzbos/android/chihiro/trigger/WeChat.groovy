package cn.wzbos.android.chihiro.trigger

import cn.wzbos.android.chihiro.utils.ExecUtils
import groovy.json.JsonOutput

class WeChat extends AbsTrigger {
    String url

    WeChat(Closure closure) {
        super(closure)
    }

    WeChat(String url) {
        this.url = url
    }

    WeChat url(String url) {
        this.url = url
        return this
    }

    private static class MarkdownMsg {
        String msgtype
        MarkDown markdown

        MarkdownMsg(String content) {
            this.msgtype = "markdown"
            this.markdown = new MarkDown(content)
        }
    }

    private static class MarkDown {
        String content

        MarkDown(String content) {
            this.content = content
        }
    }
    /**
     * 发送通知到企业微信
     */
    int send(TriggerRequest request) {
        return ExecUtils.exec('curl',
                "-s",
                url,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(new MarkdownMsg(markdown(request))))
    }
}