# EchoCommon
_The shared module for all the µs of the project/product :shipit:_

## TL;DR:
The module is used as sub-module in all the µs of the product and maintains all the shared logic between the µs, mainly in the area of the Build Automation Tool (Gradle and Jenkins in this case).

### Principal Files:
- **Jenkinsfile** and **Jenkinsfile4Release**
- **Dockerfile**
- **branchSpecificConfig.properties** - holds the branch unique configuration and utilized by the [pipelineCommon.groovy](https://github.com/demo4echo/JenkinsSharedLibrary/blob/master/vars/pipelineCommon.groovy) build script
- **Gradle** related files:
  - **common.gradle** - the main shared Gradle build logic (powering each of the µs **build.gradle**)
  - **common.groovy** - all the Groovy functions used with the **common.gradle** file
- Execution environments related configuration (to be injected into the runtime environment):
  - **_.docker (folder)_** - holds docker related configuration (e.g. **config.json** file)
  - **_.kube (folder)_** - holds K8S (a.k.a. kubectl) related configuration (e.g. **config** file)
  - **_.gradle (folder)_** - holds the common setup used by the Gradle build in the µs:
    - **gradle.properties** - the common properties used by the **common.gradle** module (mainly)
    - **init.gradle** - the common initialization logic (initializing each of the µs **build.gradle**)
