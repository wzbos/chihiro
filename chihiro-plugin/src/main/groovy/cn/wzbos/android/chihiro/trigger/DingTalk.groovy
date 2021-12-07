package cn.wzbos.android.chihiro.trigger

import cn.wzbos.android.chihiro.utils.ExecUtils
import groovy.json.JsonOutput

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class DingTalk extends AbsTrigger {
    String url
    String secret

    DingTalk(Closure closure) {
        super(closure)
    }

    DingTalk url(String url) {
        this.url = url
        return this
    }

    DingTalk secret(String secret) {
        this.secret = secret
        return this
    }

    /**
     * 发送通知到钉钉
     */
    int send(TriggerRequest request) {
        Long timestamp = System.currentTimeMillis()
        String stringToSign = timestamp + "\n" + secret
        Mac mac = Mac.getInstance("HmacSHA256")
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"))
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"))

        String sign = URLEncoder.encode(signData.encodeBase64().toString(), "UTF-8")

        return ExecUtils.exec('curl',
                "-s",
                url + '&timestamp=' + timestamp + '&sign=' + sign,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(new MarkdownMsg(markdown(request))))
    }


    private static class MarkdownMsg {
        String msgtype
        MarkDown markdown

        MarkdownMsg(String text) {
            this.msgtype = "markdown"
            this.markdown = new MarkDown(text)
        }
    }

    private static class MarkDown {
        String title
        String text
        At at

        MarkDown(String text) {
            this.title = "Publish Success!"
            this.text = text
            this.at = new At()
        }
    }

    private static class At {
        String isAtAll
        List<String> atMobiles

        At() {
            this.isAtAll = true
        }

        At(List<String> atMobiles) {
            this()
            this.atMobiles = atMobiles
        }
    }
}
