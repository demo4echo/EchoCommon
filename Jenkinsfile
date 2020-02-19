pipeline {
	agent {
		kubernetes {
			cloud resolveCloudNameByBranchName()
			label 'jenkins-slave-pod-agent'
			defaultContainer 'jdk-gradle-docker-k8s-helm'
//			yamlFile 'Jenkinsfile.JenkinsSlaveManifest.yaml'
//			namespace resolveNamespaceByBranchName()
			yaml """
apiVersion: v1
kind: Pod
metadata:
  name: jenkins-slave
  labels:
    slave-agent: jenkins
spec:
  containers:
  - name: jdk-gradle-docker-k8s-helm
    image: demo4echo/alpine_openjdk8_k8scdk
    imagePullPolicy: Always
    command:
    - cat
    tty: true
    env:
    - name: CONTAINER_ENV_VAR
      value: jdk-gradle-docker-k8s-helm
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run/docker.sock
    - name: gradle-cache-vol
      mountPath: /root/.gradle
    - name: helm-cache-vol
      mountPath: /root/.helm
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
  - name: gradle-cache-vol
    hostPath:
      path: /root/.gradle
  - name: helm-cache-vol
    hostPath:
      path: /root/.helm
"""
		}
	}
	options { 
		timestamps()
		
		buildDiscarder(logRotator(numToKeepStr: '25'))
	}
	environment {
		// We use this dummy environment variable to load all the properties from the designated file into environment variable (per brach)
		// This is indeed a pseudo comment 4 None
		X_EFRAT_ECHO_DUMMY_ENV_VAR = assimilateEnvironmentVariables()

		// Obtain the access token Jenkins uses to connect to GitHub (using a Jenkins credentials ID)
		GITHUB_ACCESS_TOKEN = credentials('github-demo4echo-access-token-for-reckon-gradle-plugin-id')
	}
	parameters {
		string(name: 'TARGET_JENKINSFILE_FILE_NAME', defaultValue: 'Jenkinsfile', description: 'The desired Jenkinsfile to run')

		string(name: 'TARGET_RECKON_SCOPE', defaultValue: "${env.JENKINS_SLAVE_K8S_RECKON_SCOPE}", description: 'The desired reckon scope to use in the build')

//		text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')

//		booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')

//		choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')

//		password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
	}	
	stages {
		stage('\u2776 setup \u2728') {//\u1F4A1
			steps {
				sh 'whoami'

				sh "echo 'The reckon scope is ${params.TARGET_RECKON_SCOPE}'"

				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.docker /root/.docker"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.kube /root/.kube"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.gradle/gradle.properties /root/.gradle/gradle.properties"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.gradle/init.gradle /root/.gradle/init.gradle"

				script {
					// Ensure target namespace is resolved
					resolveNamespaceByBranchName()
				}
			}
		}
		stage('\u2777 stamp \u2728') {//\u1F6E0
			steps {
				echo 'Will be done at the end of the build!'
			}
		}
		stage('\u2778 build \u2728') {//\u1F6E0
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} dockerBuildAndPublish"
			}
		}
		stage('\u2779 package \u2728') {//\u1F4E6
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmPackage" // helmPackageAndPublish
			}
		}
		stage('\u277A install \u2728') {//\u1F3F4
			when {
				environment name: 'CLOUD_NAME', value: 'development'
			}
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmUpdate"
			}
		}
		stage('\u277B upgrade \u2728') {//\u1F3F4
			when {
				not {
					environment name: 'CLOUD_NAME', value: 'development'
				}
			}
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmUninstall"
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmUpdate"
			}
		}
		stage('\u277C verify \u2728') {
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmTestAndClean"
			}
		}
		stage('\u277D certify \u2728') {//\u1F321
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} certify"
			}
		}
		stage('\u277E uninstall \u2728') {//\u1F3F3
			when {
				environment name: 'CLOUD_NAME', value: 'development'
			}
			steps {
				sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} helmUninstall"
			}
		}
		stage('\u277F cleanup \u2728') {
			when {
				environment name: 'CLOUD_NAME', value: 'development'
			}
			steps {
				sh "kubectl delete namespace ${env.RESOLVED_NAMESPACE}"
			}
		}
	}
	post {
		always {
			echo 'One way or another, I have finished'

			// Do some cleanup
//			sh "rm /root/.gradle/gradle.properties"
//			sh "rm /root/.gradle/init.gradle"
		}
		success {
			echo 'I succeeeded!'

			// Mark the version (done at the end, otherwise all other stages apart from the first one will get other version numbers)
			sh "./gradlew -Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} -Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} -Dorg.ajoberstar.grgit.auth.username=${env.GITHUB_ACCESS_TOKEN} publishVersion"

			// Collect JUnit test results
			junit 'build/test-results/**/*.xml'
		}
		unstable {
			echo 'I am unstable :/'
		}
		failure {
			echo 'I failed :('
//			archiveArtifacts artifacts: 'Jenkinsfile', fingerprint: true
		}
		changed {
			echo 'Things were different before...'
		}
	}
}

//
// Determine the applicable k8s cloud (towards Jenkins' configuration of the K8S plugin)
//
def resolveCloudNameByBranchName() {
	node {
//	node(env.NODE_NAME) {
//	node('master') {
		println "Within resolveCloudNameByBranchName() => Jenkins node name is: [${env.NODE_NAME}]"

		println "Branch name is: [${env.BRANCH_NAME}]"

		// Note: don't use ENV VARs here since they can't be read from their file at this stage!
		if (env.BRANCH_NAME == 'master') {
			env.CLOUD_NAME = 'production'
		} else if (env.BRANCH_NAME == 'integration') {                 
			env.CLOUD_NAME = 'staging'
		}
		else {
			env.CLOUD_NAME = 'development'		    
		}
		
		println "Resolved cloud name is: [${env.CLOUD_NAME}]"
		
		// Return the resolved cloud name
		return env.CLOUD_NAME
	}
}

//
// Determine the namespace the micro service is running in (currently the Jenkins Slave Pod is running in the default namespace)
//
def resolveNamespaceByBranchName() {
	node {
		println "Within resolveNamespaceByBranchName() => Jenkins node name is: [${env.NODE_NAME}]"

		println "Branch name is: [${env.BRANCH_NAME}]"
		println "Production branch name ENV_VAR is: [${env.PRODUCTION_BRANCH_NAME_ENV_VAR}]"
		println "Staging branch name ENV_VAR is: [${env.STAGING_BRANCH_NAME_ENV_VAR}]"

		// If we are on the production or staging branches return the regular name (e.g. demo4echo), else return the branch namne itself
		if (env.BRANCH_NAME == env.PRODUCTION_BRANCH_NAME_ENV_VAR || env.BRANCH_NAME == env.STAGING_BRANCH_NAME_ENV_VAR) {                 
			env.RESOLVED_NAMESPACE = env.SERVICE_NAME_ENV_VAR
		}
		else {
			env.RESOLVED_NAMESPACE = env.BRANCH_NAME
		}
		
		println "Resolved namespace is: [${env.RESOLVED_NAMESPACE}]"
		
		// Return the resolved namespsace
		return env.RESOLVED_NAMESPACE
	}
}

//
// Load all the properties in the per brnach designated file as environment variables
//
def assimilateEnvironmentVariables() {
//	node(env.NODE_NAME) {
//		checkout(scm) => don't need it as we'll call the function after the repository has been fetched (checkout(scm) is called in the 'agent' phase)

		println "Within assimilateEnvironmentVariables() => Jenkins node name is: [${env.NODE_NAME}]"

		def selfProps = readProperties interpolate: true, file: 'EnvFile.properties'
		selfProps.each {
			key,value -> env."${key}" = "${value}" 
		}
		
		println "JENKINS_SLAVE_K8S_DEPLOYMENT_CLOUD_NAME value is: [${env.JENKINS_SLAVE_K8S_DEPLOYMENT_CLOUD_NAME}]"
		println "JENKINS_SLAVE_K8S_RECKON_SCOPE value is: [${env.JENKINS_SLAVE_K8S_RECKON_SCOPE}]"
		println "JENKINS_SLAVE_K8S_RECKON_STAGE value is: [${env.JENKINS_SLAVE_K8S_RECKON_STAGE}]"
		println "JENKINS_SLAVE_K8S_GIT_STORE_ACCESS_TOKEN_NAME value is: [${env.JENKINS_SLAVE_K8S_GIT_STORE_ACCESS_TOKEN_NAME}]"

		// Manifest common sub module folder name
		def commonSubModuleFolderName = locateCommonSubModuleFolderName()
		env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR = commonSubModuleFolderName
		println "COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR value is: [${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}]"

		return env.JENKINS_SLAVE_K8S_DEPLOYMENT_CLOUD_NAME
//	}
}

//
// Locate sub module folder name
//
def locateCommonSubModuleFolderName() {
	println "Within locateCommonSubModuleFolderName() => Jenkins node name is: [${env.NODE_NAME}]"

	def markupFiles = findFiles(glob: '**/_CommonSubModulePickup.markup')
	def commonSubModuleMarkupFileRelativePath = markupFiles[0].path
	def (commonSubModuleFolderName,commonSubModulePickupFileName) = commonSubModuleMarkupFileRelativePath.tokenize('/')
	def commonSubModuleName = commonSubModuleFolderName

/**
	def baseDir = new File('.')

	// Traverse the sub folders of the current folder
	baseDir.eachDir {
		def targetFilePath = "." + File.separator + it.name + File.separator + COMMON_SUB_MODULE_MARKER_FILE_NAME
		def currentFile = new File(targetFilePath)
		
		if (currentFile.exists() == true) {
			commonSubModuleName = it.name
		}
	}
*/
	return commonSubModuleName
}