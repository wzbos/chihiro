package cn.wzbos.android.chihiro.settings

/**
 * Chihiro Module
 * Created by wuzongbo on 2020/09/02.
 */
class ChihiroModule {
    /**
     * module名称
     */
    private String name
    /**
     * repository
     */
    private String repository

    String getGroup() {
        if (repository == null)
            return ""

        String[] arrayOfStrings = repository.split(":")
        if (arrayOfStrings.length < 1)
            return ""

        return arrayOfStrings[0]
    }

    String getArtifactId() {
        if (repository == null)
            return ""

        String[] arrayOfStrings = repository.split(":")
        if (arrayOfStrings.length < 2)
            return ""

        return arrayOfStrings[1]
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
                "name='" + name + '\'' +
                ", repository='" + repository + '\'' +
                '}';
    }
}
