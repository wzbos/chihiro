package cn.wzbos.android.chihiro.mvn


import cn.wzbos.android.chihiro.utils.Logger
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
        if ("publish".equalsIgnoreCase(task.name)) {
            Logger.i("${task.project.name} Publish...")
        }
    }

    List<MvnConfig> archives = new ArrayList<>()

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.project.plugins.hasPlugin('maven-publish') && "publish".equalsIgnoreCase(task.name)) {
            if (taskState.executed) {
                if (taskState.failure == null) {
                    Logger.i("publish ${task.project.name} success!")
                    MvnConfig mvnConfig = MvnConfig.load(task.project)
                    archives.add(mvnConfig)
                } else {
                    Logger.e("publish ${task.project.name} failed!")
                }
            }
        }
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
            if (settings != null) {
                settings.send(projectName, archives)
            }
        }
        archives.clear()
    }

}
