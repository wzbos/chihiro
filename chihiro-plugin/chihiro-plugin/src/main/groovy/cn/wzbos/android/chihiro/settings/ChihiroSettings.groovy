package cn.wzbos.android.chihiro.settings

/**
 * Chihiro Settings
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroSettings {
    final static String GRADLE_NAME = "chihiro.gradle"

    private boolean log = false
    /**
     * 千寻插件工程配置集合
     */
    private List<ChihiroProject> projects

    boolean isDebug(String projectName) {
        if (projects == null)
            return false

        for (ChihiroProject project : projects) {
            if (project.name.equalsIgnoreCase(projectName)) {
                return project.debug
            }
        }
        return false
    }

    String getLocalProjectName(String rootProjectName, String group, String module) {
        if (projects == null)
            return null

        for (ChihiroProject project : projects) {
            if (project.name.equalsIgnoreCase(rootProjectName)) {
                println("[Chihiro] getLocalProjectName,is root project!")
                return null
            }
            if (project.modules == null)
                return null

            for (ChihiroModule m : project.modules) {
                if (m.group == group) {
                    if (m.artifactId == module) {
                        return project.name
                    }
                }
            }
        }
        return null
    }

    List<ChihiroProject> getProjects() {
        return projects
    }

    void setProjects(List<ChihiroProject> projects) {
        this.projects = projects
    }

    boolean getLog() {
        return log
    }

    void setLog(boolean log) {
        this.log = log
    }


    @Override
    String toString() {
        return "ChihiroSettings{" +
                "log=" + log +
                ", projects=" + projects +
                '}'
    }
}