package cn.wzbos.android.chihiro

import cn.wzbos.android.chihiro.exception.ChihiroException
import cn.wzbos.android.chihiro.mvn.AndroidMvnPlugin
import cn.wzbos.android.chihiro.mvn.JavaMvnPlugin
import cn.wzbos.android.chihiro.settings.ChihiroSettings
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository

/**
 * 千寻插件(module)
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroPlugin implements Plugin<Project> {

    ChihiroSettings settings;
    Project pro

    @Override
    void apply(Project pro) {
        this.pro = pro
        if (!pro.plugins.hasPlugin('com.android.application') && !pro.plugins.hasPlugin('com.android.library') && !pro.plugins.hasPlugin('java')) {
            throw new IllegalStateException('插件仅支持 Android 或 Java Library!')
        }

        settings = pro.extensions.create('chihiro', ChihiroSettings)

        //导入配置文件(以rootProject配置为准)
        def debugGradleFile = pro.rootDir.path + File.separator + ChihiroSettings.GRADLE_NAME
        log("import debug.gradle:$debugGradleFile")
        if (new File(debugGradleFile).exists()) {
            pro.apply from: debugGradleFile
        }

        log("settings: ${settings}")
        addMavenRepositories()
        cacheChangingModulesTime(0)
        addDynamicDSLMethod()
        applyMvnPlugin()
        replaceDependency()
    }

    void log(String msg) {
        if (settings != null && settings.log)
            println("[Chihiro] $msg")
    }

    private void addDynamicDSLMethod() {
        pro.rootProject.ext {
            dynamic = { path ->
                def parentPrjName = pro.getParent().name
                log("dynamic, project:$parentPrjName,path:$path")
                if (settings != null && settings.isDebug(parentPrjName)) {
                    try {
                        log("dynamic($path) ==> project(:${parentPrjName}${path})")
                        return pro.project(":${parentPrjName}${path}")
                    } catch (UnknownProjectException exception) {
                        throw new ChihiroException("组件\"${parentPrjName}${path}\"不存在！", exception)
                    }
                } else {
                    /*
                     * 提示：如果此处报错，则是因为参数问题,请参考如下格式
                     * implementation dynamic(":library")
                     */
                    try {
                        return pro.project(path)
                    } catch (UnknownProjectException exception) {
                        throw new ChihiroException("组件\"${path}\"不存在！", exception)
                    }
                }
            }
        }
    }

    private void applyMvnPlugin() {
        pro.afterEvaluate {
            if (new File(pro.projectDir.path + "/gradle.properties").exists()) {
                if (pro.plugins.hasPlugin('com.android.library')) {
                    log("Mvn:${pro.name} ==> AndroidMvnPlugin")
                    pro.apply plugin: AndroidMvnPlugin
                    pro.android.defaultConfig {
                        buildConfigField("String", "BUILD_TIME", "\"${new Date().format("yyyy/MM/dd HH:mm:ss")}\"")
                        buildConfigField("String", "BUILD_CODE", "\"${System.currentTimeMillis()}\"")
                    }
                } else if (pro.plugins.hasPlugin('java')) {
                    log("Mvn:${pro.name} ==> JavaMvnPlugin")
                    pro.apply plugin: JavaMvnPlugin
                }
            }
        }
    }

    private void replaceDependency() {
        pro.afterEvaluate {
            pro.configurations.all {
                resolutionStrategy.dependencySubstitution {
                    all { DependencySubstitution dependency ->
                        if (dependency.requested instanceof ModuleComponentSelector) {
                            ModuleComponentSelector selector = dependency.requested
                            if (settings != null) {
                                String prjName = settings.getLocalProjectName(pro.rootProject.project.name, selector.group, selector.module)
                                if (prjName != null && prjName.length() > 0) {
                                    log("${selector} ==> project(:${prjName}:${selector.module})")
                                    dependency.useTarget pro.project(":${prjName}:${selector.module}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void addMavenRepositories() {
        if (pro.hasProperty("MAVEN_SNAPSHOTS_URL"))
            addMavenRepositories(pro.MAVEN_SNAPSHOTS_URL)
        if (pro.hasProperty("MAVEN_RELEASE_URL"))
            addMavenRepositories(pro.MAVEN_RELEASE_URL)
    }

    void addMavenRepositories(String mvnUrl) {
        int count = pro.repositories.size()
        for (int i = 0; i < count; i++) {
            ArtifactRepository repository = pro.repositories.get(i)
            if (repository instanceof MavenArtifactRepository) {
                DefaultMavenArtifactRepository mvnRepository = (DefaultMavenArtifactRepository) repository;
                if (mvnRepository.url.toString().equalsIgnoreCase(mvnUrl)) {
                    return
                }
            }
        }

        MavenArtifactRepository mavenRepository = pro.repositories.maven {
            url mvnUrl
        }
        log("addMavenRepositories,mvnUrl:$mvnUrl")
        pro.repositories.remove(mavenRepository)
        pro.repositories.add(0, mavenRepository)
    }

    void cacheChangingModulesTime(int sec) {
        pro.configurations.all {
            resolutionStrategy.cacheChangingModulesFor sec, 'seconds'
        }
    }

}


