package cn.wzbos.android.chihiro.mvn

import cn.wzbos.android.chihiro.WeChat
import cn.wzbos.android.chihiro.settings.ChihiroSettings
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
class PublishListener implements TaskExecutionListener, BuildListener {

    ChihiroSettings settings

    PublishListener(ChihiroSettings settings) {
        this.settings = settings
    }

    @Override
    void beforeExecute(Task task) {
        if ("uploadArchives".equalsIgnoreCase(task.name)) {
            task.logger.info("[Chihiro] ${task.project.name} Publish...")
        }
    }

    List<String> archives = new ArrayList<>()

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.project.plugins.hasPlugin('maven') && "uploadArchives".equalsIgnoreCase(task.name)) {
            if (taskState.executed) {
                if (taskState.failure == null) {
                    task.logger.warn("\033[32m[Chihiro] uploadArchives ${task.project.name} success!\033[0m")
                    MvnConfig mvnConfig = MvnConfig.load(task.project)
                    archives.add("<font color=\\\"#0083FF\\\">${mvnConfig.group}</font>:<font color=\\\"info\\\">${mvnConfig.artifactId}</font>:<font color=\\\"#FF0000\\\">${mvnConfig.version}</font>")
                } else {
                    task.logger.error("\033[31m[Chihiro] uploadArchives ${task.project.name} failed!\033[0m")
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
            println("\033[32m[Chihiro] uploadArchives complete!\033[0m")
            if (settings != null && settings.wechat_key != null && settings.wechat_key.length() > 0) {
                String content = "### Publish <font color=\\\"#FF0000\\\">${projectName}</font> Success!";
                content += "\n- Builder：${gitUsername}"
                content += "\n- Branch：${gitBranch}"
                content += "\n- Time：${new Date().format("yyyy-MM-dd HH:mm:ss")}"
                if (archives != null) {
                    for (String archive : archives) {
                        content += "\n> ${archive}"
                    }
                }
                WeChat.sendNotify(result.gradle.rootProject, settings.wechat_key, content)
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
