package cn.wzbos.android.chihiro.settings

import cn.wzbos.android.chihiro.mvn.MvnConfig

/**
 * Chihiro Project
 * Created by wuzongbo on 2020/09/07.
 */
class ChihiroProject {
    boolean debug
    String directory
    String git
    String branch
    List<MvnConfig> modules

    @Override
    String toString() {
        return "ChihiroProject{" +
                "debug=" + debug +
                ", directory='" + directory + '\'' +
                ", git='" + git + '\'' +
                ", branch='" + branch + '\'' +
                ", modules=" + modules +
                '}';
    }
}
