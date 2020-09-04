package cn.wzbos.android.chihiro.settings

/**
 * Chihiro Module
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroModule {
    /**
     * 组件所属组
     * 例如：cn.wzbos.android:chihiro:1.0.0，其中cn.wzbos.android为group
     */
    private String group
    /**
     * 组件artifactId
     * 例如：cn.wzbos.android:chihiro:1.0.0，其中chihiro为artifactId
     */
    private String artifactId
    /**
     * module名称
     */
    private String name

    String getGroup() {
        return group
    }

    void setGroup(String group) {
        this.group = group
    }

    String getArtifactId() {
        return artifactId
    }

    void setArtifactId(String artifactId) {
        this.artifactId = artifactId
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Override
    String toString() {
        return "ChihiroModule{" +
                "group='" + group + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
