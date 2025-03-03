package cn.wzbos.android.chihiro

import cn.wzbos.android.chihiro.exception.ChihiroException
import cn.wzbos.android.chihiro.mvn.MvnPlugin
import cn.wzbos.android.chihiro.settings.ChihiroSettings
import cn.wzbos.android.chihiro.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.component.ModuleComponentSelector

/**
 * 千寻插件(module)
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroPlugin implements Plugin<Project> {

    def settings
    Project pro

    @Override
    void apply(Project pro) {
        this.pro = pro
        if (!pro.plugins.hasPlugin('com.android.application') && !pro.plugins.hasPlugin('com.android.library') && !pro.plugins.hasPlugin('java')) {
            throw new IllegalStateException('插件仅支持 Android 或 Java Library!')
        }
        settings = ChihiroSettings.get(pro)

        cacheChangingModulesTime(0)
        addDynamicDSLMethod()
        applyMvnPlugin()
        replaceDependency()
    }

    private void addDynamicDSLMethod() {
        if (pro.buildscript.sourceFile.absolutePath.endsWith(".kts")) {
            // 注册 Kotlin DSL 扩展函数
            pro.extensions.add("dynamic", this.&dynamic)
        } else {
            // Groovy DSL 使用 ExtraPropertiesExtension
            pro.rootProject.ext {
                dynamic = { path ->
                    dynamic(path)
                }
            }
        }
    }

    private Object dynamic(path) {
        def parentPrjName = pro.getParent().name
        Logger.d("dynamic, project=$parentPrjName, path=$path")
        if (settings != null && settings.isDebug(parentPrjName)) {
            try {
                Logger.i("dynamic($path) ==> project(:${parentPrjName}${path})")
                return pro.project(":${parentPrjName}${path}")
            } catch (UnknownProjectException exception) {
                throw new ChihiroException("组件\"${parentPrjName}${path}\"不存在！", exception)
            }
        } else {
            try {
                return pro.project(path)
            } catch (UnknownProjectException exception) {
                throw new ChihiroException("组件\"${path}\"不存在！", exception)
            }
        }
    }

    private void applyMvnPlugin() {
        if (settings != null && settings.maven) {
            pro.afterEvaluate {
                if (new File(pro.projectDir.path + "/gradle.properties").exists()) {
                    if (pro.plugins.hasPlugin('com.android.library') || pro.plugins.hasPlugin('java')) {
                        pro.apply plugin: MvnPlugin
                    }
                }
            }
        }
    }

    private void replaceDependency() {
        pro.afterEvaluate {
            pro.configurations.configureEach {
                resolutionStrategy.dependencySubstitution {
                    all { DependencySubstitution dependency ->
                        if (dependency.requested instanceof ModuleComponentSelector) {
                            ModuleComponentSelector selector = dependency.requested
                            if (settings != null) {
                                try {
                                    String prjName = settings.getLocalProjectName(selector.group, selector.module)
                                    if (prjName != null && prjName.length() > 0) {
                                        Logger.i("${selector} ==> project(\":${prjName}:${selector.module}\")")
                                        dependency.useTarget pro.project(":${prjName}:${selector.module}")
                                    }
                                } catch (Exception e) {
                                    throw new ChihiroException("replaceDependency failed!", e)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    void cacheChangingModulesTime(int sec) {
        pro.configurations.configureEach {
            resolutionStrategy.cacheChangingModulesFor sec, 'seconds'
        }
    }
}

