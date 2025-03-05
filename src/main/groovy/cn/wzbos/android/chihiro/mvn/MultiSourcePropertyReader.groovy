package cn.wzbos.android.chihiro.mvn;


import org.gradle.api.Project


class MultiSourcePropertyReader {
    Project project = null
    Properties properties = null
    Properties localProperties = null

    MultiSourcePropertyReader(Project project, Properties properties, Properties localProperties) {
        this.project = project
        this.properties = properties
        this.localProperties = localProperties
    }

    String getStringProperty(String... keys) {
        for (String key : keys) {
            if (project != null && project.hasProperty(key)) {
                return String.valueOf(project.findProperty(key))
            } else if (properties != null && properties.containsKey(key)) {
                return properties.getProperty(key)
            } else if (localProperties != null && localProperties.containsKey(key)) {
                return localProperties.getProperty(key)
            }
        }
        return null
    }

    boolean getBooleanProperty(String... keys) {
        for (String key : keys) {
            if (project != null && project.hasProperty(key)) {
                return Boolean.parseBoolean(String.valueOf(project.findProperty(key)))
            } else if (properties != null && properties.containsKey(key)) {
                return Boolean.parseBoolean(properties.getProperty(key))
            } else if (localProperties != null && localProperties.containsKey(key)) {
                return Boolean.parseBoolean(localProperties.getProperty(key))
            }
        }
        return null
    }
}
