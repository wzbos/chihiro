package cn.wzbos.android.chihiro.trigger

import cn.wzbos.android.chihiro.mvn.MvnConfig


abstract class AbsTrigger {

    AbsTrigger() {
    }

    AbsTrigger(Closure closure) {
        closure.setDelegate(this)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        if (closure.getMaximumNumberOfParameters() == 0) {
            closure.call()
        } else {
            closure.call(this)
        }
    }

    static String markdown(TriggerRequest request) {
        String content = "### <font color=\"#FF0000\">${request.project}</font> Published Successfully!"
        content += "\n- Builder：${request.username}"
        content += "\n- Branch：${request.branch}"
        content += "\n- Time：${request.datetime}"
        if (request.archives != null) {
            for (MvnConfig archive : request.archives) {
                content += "\n> <font color=\"#0083FF\">${archive.group}</font>:<font color=\"info\">${archive.artifactId}</font>:<font color=\"#FF0000\">${archive.version}</font>"
            }
        }
        return content
    }

    abstract int send(TriggerRequest request)
}