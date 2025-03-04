package cn.wzbos.android.chihiro.trigger

import cn.wzbos.android.chihiro.mvn.MvnConfig
import cn.wzbos.android.chihiro.utils.GitUtils

class TriggerRequest {
    String project
    String username
    String branch
    String datetime
    List<MvnConfig> archives

    TriggerRequest(String projectName, List<String> archives) {
        this.branch = GitUtils.gitBranch
        this.username = GitUtils.gitUsername
        this.datetime = new Date().format("yyyy-MM-dd HH:mm:ss")
        this.project = projectName
        this.archives = archives
    }

}