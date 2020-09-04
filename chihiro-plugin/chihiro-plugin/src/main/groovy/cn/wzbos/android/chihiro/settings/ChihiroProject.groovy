package cn.wzbos.android.chihiro.settings

/**
 * Chihiro Project
 * Created by wuzongbo on 2020/09/07.
 */
class ChihiroProject {
    private String name
    private boolean debug
    private List<ChihiroModule> modules

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    boolean getDebug() {
        return debug
    }

    void setDebug(boolean debug) {
        this.debug = debug
    }

    List<ChihiroModule> getModules() {
        return modules
    }

    void setModules(List<ChihiroModule> modules) {
        this.modules = modules
    }

    @Override
    String toString() {
        return "ChihiroProject{" +
                "name='" + name + '\'' +
                ", debug=" + debug +
                ", modules=" + modules +
                '}';
    }
}
