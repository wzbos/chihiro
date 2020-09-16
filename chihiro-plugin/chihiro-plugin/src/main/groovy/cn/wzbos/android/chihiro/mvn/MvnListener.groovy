package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.utils.WebHookUtils
import cn.wzbos.android.chihiro.utils.Logger
import cn.wzbos.android.chihiro.utils.TextUtils
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

/**
 * 组件发布监听
 * Created by wuzongbo on 2020/09/14.
 */
class MvnListener implements TaskExecutionListener, BuildListener {


    @Override
    void beforeExecute(Task task) {
        if ("uploadArchives".equalsIgnoreCase(task.name)) {
            Logger.i("${task.project.name} Publish...")
        }
    }

    List<String> archives = new ArrayList<>()

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.project.plugins.hasPlugin('maven') && "uploadArchives".equalsIgnoreCase(task.name)) {
            if (taskState.executed) {
                if (taskState.failure == null) {
                    Logger.i("uploadArchives ${task.project.name} success!")
                    MvnConfig mvnConfig = MvnConfig.load(task.project)
                    archives.add("<font color=\\\"#0083FF\\\">${mvnConfig.group}</font>:<font color=\\\"info\\\">${mvnConfig.artifactId}</font>:<font color=\\\"#FF0000\\\">${mvnConfig.version}</font>")
                } else {
                    Logger.e("uploadArchives ${task.project.name} failed!")
                }
            }
        }
    }

    @Override
    void buildStarted(Gradle gradle) {

    }

    @Override
    void settingsEvaluated(Settings settings) {

    }

    @Override
    void projectsLoaded(Gradle gradle) {

    }

    @Override
    void projectsEvaluated(Gradle gradle) {

    }

    @Override
    void buildFinished(BuildResult result) {
        def projectName = result.gradle.rootProject.name
        if (result.failure == null && archives.size() > 0) {
            Logger.i("publish complete!")

            def settings = result.gradle.ext.chihiroSettings
            if (settings != null && !TextUtils.isEmpty(settings.wechat_key)) {
                String content = "### Publish <font color=\\\"#FF0000\\\">${projectName}</font> Success!";
                content += "\n- Builder：${gitUsername}"
                content += "\n- Branch：${gitBranch}"
                content += "\n- Time：${new Date().format("yyyy-MM-dd HH:mm:ss")}"
                if (archives != null) {
                    for (String archive : archives) {
                        content += "\n> ${archive}"
                    }
                }
                WebHookUtils.sendToWeChat(result.gradle.rootProject, settings.wechat_key, content)
            }
            archives.clear()
        }
    }

    static String getGitBranch() {
        try {
            return 'git symbolic-ref --short -q HEAD'.execute().text.trim()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return ""
    }

    static String getGitUsername() {
        try {
            return 'git config user.name'.execute().text.trim()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return ""
    }
}
