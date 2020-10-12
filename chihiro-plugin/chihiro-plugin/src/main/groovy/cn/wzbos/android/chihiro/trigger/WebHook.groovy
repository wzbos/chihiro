package cn.wzbos.android.chihiro.trigger

import cn.wzbos.android.chihiro.utils.ExecUtils
import groovy.json.JsonOutput

class WebHook extends AbsTrigger {
    String url

    WebHook(Closure closure) {
        super(closure)
    }

    WebHook url(String url) {
        this.url = url
        return this
    }

    /**
     * 发送自定义WebHook
     */
    int send(TriggerRequest request) {
        return ExecUtils.exec('curl',
                "-s",
                url,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(request))
    }
}