package cn.wzbos.android.chihiro.mvn;


import org.gradle.api.Project


class MultiSourcePropertyReader {
    Project project = null
    Properties moduleProperties = null
    Properties localProperties = null

    private static Properties loadPropertyFile(File file) {
        Properties properties = null
        if (file != null && file.exists()) {
            properties = new Properties()
            try (FileInputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream)
            }
        }
        return properties
    }

    MultiSourcePropertyReader(Project project, File modulePropertiesFile, File localPropertiesFile) {
        this.project = project
        this.moduleProperties = loadPropertyFile(modulePropertiesFile)
        this.localProperties = loadPropertyFile(localPropertiesFile)
    }


    String getStringProperty(String... keys) {
        for (String key : keys) {
            if (project != null && project.hasProperty(key)) {
                return String.valueOf(project.findProperty(key))
            } else if (moduleProperties != null && moduleProperties.containsKey(key)) {
                return moduleProperties.getProperty(key)
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
            } else if (moduleProperties != null && moduleProperties.containsKey(key)) {
                return Boolean.parseBoolean(moduleProperties.getProperty(key))
            } else if (localProperties != null && localProperties.containsKey(key)) {
                return Boolean.parseBoolean(localProperties.getProperty(key))
            }
        }
        return null
    }
}
