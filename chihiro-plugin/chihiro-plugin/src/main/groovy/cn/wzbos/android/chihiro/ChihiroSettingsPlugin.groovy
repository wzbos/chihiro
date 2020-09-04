package cn.wzbos.android.chihiro


import cn.wzbos.android.chihiro.settings.ChihiroModule
import cn.wzbos.android.chihiro.settings.ChihiroProject
import cn.wzbos.android.chihiro.settings.ChihiroSettings
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * 千寻插件（Project）
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroSettingsPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        ChihiroSettings extension = settings.extensions.create('chihiro', ChihiroSettings)

        //加载本地配置文件
        def debugGradleFile = settings.rootDir.path + File.separator + ChihiroSettings.GRADLE_NAME
        println("debugGradleFile:${debugGradleFile}")

        if (new File(debugGradleFile).exists()) {
            settings.apply from: debugGradleFile
        }

        if (extension == null) {
            return
        }
        println("extension:${extension}")

        if (extension.projects == null)
            return

        for (ChihiroProject project in extension.projects) {
            println("ChihiroProject:${project}")
            def projectName = project.name
            if (!project.debug)
                continue

            def prj_dir = new File("${settings.rootDir.getParentFile().path}/${projectName}")
            def exist = prj_dir.exists()
            if (!exist) {
                println("项目 $prj_dir 不存在！")
                continue
            }
            println("--------------------------------------------------------------")
            println("include $projectName")
            settings.include "$projectName"
            println("project(\":${projectName}\").projectDir = ${prj_dir}")
            settings.project(":${projectName}").projectDir = prj_dir
            if (project.modules != null) {
                println("--------------------------[MODULES]---------------------------")
                for (ChihiroModule m in project.modules) {
                    println("include :${projectName}:${m.name}")
                    settings.include ":${projectName}:${m.name}"
                }
            }
            println("--------------------------------------------------------------")

        }
    }

}