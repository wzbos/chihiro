# chihiro-plugin

千寻安卓组件快速开发插件 android chihiro plugin


 [ ![Download](https://api.bintray.com/packages/wuzongbo/maven/chihiro-plugin/images/download.svg) ](https://bintray.com/wuzongbo/maven/chihiro-plugin/_latestVersion)


本插件是解决基于Sonatype Nexus Repository Manager OSS仓库 快速开发Android组件提供的一套完整的解决方案，
能大大提高组件开发、调试效率

## 特性

1.多组件开发无需频繁发布SNAPSHOT包
2.支持多组件代码依赖方式联调，及同一工程，同一IDE（Android studio）加载多个Android Project
3.解决发布组件时出现unspecified版本问题，再也不必按照依赖顺序发布每个组件
4.组件配置更简单，无需每个组件去配置Maven上传脚本


## 快速接入

### 一、插件接入

#### 在module/build.gradle文件中添加如下配置

```gradle

apply plugin: 'chihiro'

repositories {
    jcenter()
}

```

### 二、创建组件

#### 1、创建组件配置文件

在library组件目录下添加 gradle.properties 文件，内容如下

```properties
# 组件发布后的格式：
# dependencies {
#   implementation "${PROJ_GROUP}:${PROJ_ARTIFACTID}:${PROJ_VERSION}"
# }
# 组件名
PROJ_NAME=library1
# 组件所属组
PROJ_GROUP=cn.wzbos.android.library
# 组件ID
PROJ_ARTIFACTID=library1
# 组件版本名称
PROJ_VERSION=1.0.0
# 组件版本号
PROJ_VERSION_CODE=1
# 工程名称
PROJ_POM_NAME=library1
# 组件说明
PROJ_DESCRIPTION=Lib For wzbos.cn
# 组件发布类型，aar或者jar
POM_PACKAGING=aar
# 组件gitlab仓库地址
PROJ_WEBSITEURL=https\://github.com/wzbos/chihiro
# issues 地址
PROJ_ISSUETRACKERURL=
# Git仓库地址
PROJ_VCSURL=
# 开发者ID
DEVELOPER_ID=
# 开发者名字
DEVELOPER_NAME=
# 开发者邮箱
DEVELOPER_EMAIL=
```

#### 2、配置Maven仓库地址与用户名密码

将以下配置放入project/gradle.properties 文件下，也可以将配置放入每个组件的gradle.properties文件内

```properties
# MAVEN RELEASE仓库地址
MAVEN_RELEASE_URL=http\://xxx/repository/maven-releases
# MAVEN SNAPSHOTS仓库地址
MAVEN_SNAPSHOTS_URL==http\://xxx/repository/maven-releases
# MAVEN 登录名
MAVEN_USERNAME=xxx
# MAVEN 密码
MAVEN_PWD=xxx
```

### 三、发布组件

在Terminal终端内执行如下命令

```bash
./gradlew clean assembleRelease uploadArchives --info
```

### 四、多组件工程调试

我们在开发组件中可能依赖一个同时处于开发阶段的另一个工程内的组件，常见的开发形式是另一个工程通过不停的发布SNAPSHOT版本然后本工程不停的拉取，
而且本地来去的版本源码有可能是一份老的代码，无法进行debug下的断点，从而大大降低开发效率。

此插件可以彻底解决以上问题，实现api形式的依赖犹如本地project组件一样丝滑。

具体步骤如下：


#### 1.在调用方Module的build.gradle文件中添加如下配置

```gradle

apply plugin: 'chihiro'

repositories {
    jcenter()
}

```

#### 2.在调用方Project的settings.gradle中添加如下配置用来支持本地工程联调

```gradle

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "cn.wzbos.android:chihiro-plugin:1.x"
    }
}
apply plugin: 'chihiro-settings'
```

#### 3、配置chihiro.gradle

在调用方的Project的根目录下创建chihiro.gradle文件，并按如下格式进行编辑

例如：以下有工程"sample-sdk"，

group为组件包名(gradle.properties文件中的PROJ_GROUP值)


```gradle
/**
 * Chihiro-Plugin setting
 * github:https://github.com/wzbos/chihiro
 */
chihiro {
    //true: print debug log,false: no print
    log = true
    //set debug project
    projects = [[
                        //project directory name
                        name   : "sample-sdk",
                        //true: include the project,false: no include
                        debug  : true,
                        // all modules for project
                        modules: [[
                                          //module name
                                          name      : 'library1',
                                          //maven group ,see gradle.properties
                                          group     : 'cn.wzbos.chihiro.sample.sdk',
                                          //maven artifactId,see gradle.properties
                                          artifactId: 'library1',
                                  ], [
                                          //module name
                                          name      : 'library2',
                                          //maven group ,see gradle.properties
                                          group     : 'cn.wzbos.chihiro.sample.sdk',
                                          //maven artifactId,see gradle.properties
                                          artifactId: 'library2',
                                  ], [
                                          //module name
                                          name      : 'library3',
                                          //maven group ,see gradle.properties
                                          group     : 'cn.wzbos.chihiro.sample.sdk',
                                          //maven artifactId,see gradle.properties
                                          artifactId: 'library3',
                                  ]]

                ]
    ]
}
```

#### 4、修改本地组件依赖

将本地组件之间的project依赖改为dynamic依赖

如：

```gradle
dependencies {
    implementation project(":library1")
}
```

改为

```gradle
dependencies {
    implementation dynamic(":library1")
}
```

#### 5、同步工程
点击 Android Studio -> File -> Sync Project with Gradle Files 同步整个工程即可看到

