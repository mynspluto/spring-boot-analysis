# 목적

오픈 소스 spring-boot(https://github.com/spring-projects/spring-boot) 분석

# 방법

4.0.x 브랜치의 내용을 이 프로젝트에 main 브랜치에 복사
마지막 커밋
31a050086af1c6778348bb4de84abce2d2471056 Move server-specific knowledge out of general child context creation
Issue: 44068

각 파일, ide 내 설정, 빌드 환경, 빌드 산출물 등을 분석하여 문서화

# settings.gradle, build.gradle 용도

| package.json                  | Gradle                             |
| ----------------------------- | ---------------------------------- |
| name, version                 | settings.gradle의 rootProject.name |
| workspaces                    | settings.gradle의 include          |
| dependencies, devDependencies | build.gradle의 dependencies        |
| scripts                       | build.gradle의 tasks               |
| engines (node 버전 등)        | settings.gradle의 pluginManagement |
| repository 설정               | settings.gradle의 repositories     |

package.json의 역할을 나눠서 담당

# 인텔리제이 우측의 gradle 탭 tasks 중 npm task 등록되는 과정

AntoraConventions.java
↓
NpmInstallTask (com.github.gradle.node.npm.task.NpmInstallTask)
↓
NpmTask (com.github.gradle.node.npm.task.NpmTask)

// NpmTask.kt(https://github.com/node-gradle/gradle-node-plugin/blob/main/src/main/kotlin/com/github/gradle/node/npm/task/NpmTask.kt)에서
```java
open class NpmTask : DefaultExecAction() {
  init {
    group = NodePlugin.NPM_GROUP("npm")
  }
}
```
  
