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
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository

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

        addMavenRepositories()
        cacheChangingModulesTime(0)
        addDynamicDSLMethod()
        applyMvnPlugin()
        replaceDependency()
    }


    private void addDynamicDSLMethod() {
        pro.rootProject.ext {
            dynamic = { path ->
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
            pro.configurations.all {
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
                DefaultMavenArtifactRepository mvnRepository = (DefaultMavenArtifactRepository) repository
                if (mvnRepository.url.toString().equalsIgnoreCase(mvnUrl)) {
                    return
                }
            }
        }

        MavenArtifactRepository mavenRepository = pro.repositories.maven {
            if (mvnUrl.toLowerCase().startsWith("http")) {
                url mvnUrl
            } else {
                url pro.uri(mvnUrl)
            }
        }

        pro.repositories.remove(mavenRepository)
        pro.repositories.add(0, mavenRepository)

        Logger.i("add maven repository, ${mavenRepository.url}")
    }

    void cacheChangingModulesTime(int sec) {
        pro.configurations.all {
            resolutionStrategy.cacheChangingModulesFor sec, 'seconds'
        }
    }

}


