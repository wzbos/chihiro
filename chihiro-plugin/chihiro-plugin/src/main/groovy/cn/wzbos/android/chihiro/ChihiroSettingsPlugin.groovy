package cn.wzbos.android.chihiro

import cn.wzbos.android.chihiro.exception.ChihiroException
import cn.wzbos.android.chihiro.mvn.MvnConfig
import cn.wzbos.android.chihiro.settings.ChihiroProject
import cn.wzbos.android.chihiro.settings.ChihiroSettings
import cn.wzbos.android.chihiro.utils.GitUtils
import cn.wzbos.android.chihiro.utils.Logger
import cn.wzbos.android.chihiro.utils.TextUtils
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * 千寻插件（Project）
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroSettingsPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        ChihiroSettings chihiroSettings = ChihiroSettings.get(settings)
        if (chihiroSettings == null) {
            return
        }
        Logger.d("${chihiroSettings}")
        if (chihiroSettings.projects == null) {
            return
        }

        for (Map.Entry<String, ChihiroProject> kv in chihiroSettings.projects.entrySet()) {
            def projectName = kv.key
            def chihiroProject = kv.value
            if (!chihiroProject.debug)
                continue

            def directory = chihiroProject.directory
            if (TextUtils.isEmpty(directory))
                directory = "${settings.rootDir.getParentFile().path}/${projectName}"

            def file = new File(directory)
            def exist = file.exists()
            if (!exist) {
                if (TextUtils.isEmpty(chihiroProject.git)) {
                    throw new ChihiroException("[Chihiro] 项目 \"$file\" 不存在,请检查 ${ChihiroSettings.GRADLE_NAME} 配置!")
                }

                if (GitUtils.clone(chihiroProject.git, chihiroProject.branch, directory) > 0) {
                    Logger.e("clone failed!\n")
                    return
                } else {
                    Logger.w("clone success!\n")
                }
            }
            Logger.i("--------------------------------------------------------------")
            Logger.i("include $projectName")
            settings.include "$projectName"
            Logger.i("project(\":${projectName}\").projectDir = ${file}")

            settings.project(":${projectName}").projectDir = file

            def files = file.listFiles()
            if (files != null) {
                Logger.i("--------------------------[MODULES]---------------------------")
                for (File f : files) {
                    if (f.directory) {
                        def gradleSettingsPath = f.getPath() + File.separator + "gradle.properties"
                        if (new File(gradleSettingsPath).exists()) {
                            MvnConfig mvnConfig = MvnConfig.load(gradleSettingsPath)
                            if (mvnConfig != null && mvnConfig.isValid()) {
                                def moduleName = ":$projectName:${f.name}"
                                Logger.i("include ${moduleName}")
                                settings.include moduleName

                                if (chihiroProject.modules == null) {
                                    chihiroProject.modules = new ArrayList<>()
                                }
                                chihiroProject.modules.add(mvnConfig)
                            }
                        }
                    }
                }
                Logger.i("--------------------------------------------------------------\n")
            }
        }
        chihiroSettings.save(settings)
    }


}