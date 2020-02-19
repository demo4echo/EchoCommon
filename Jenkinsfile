// We can omit this one as we marked the shared library to load implicitly
@Library('EchoSharedLibrary') _

// Load shared resources
def jenkinsSlavePodManifestResourceAsString = libraryResource 'jenkinsSlavePodManifest.yaml'

pipeline {
	agent {
		kubernetes {
			cloud pipelineCommon.resolveCloudNameByBranchName()
			label 'jenkins-slave-pod-agent'
			defaultContainer 'jdk-gradle-docker-k8s-helm'
//			yamlFile 'Jenkinsfile.JenkinsSlaveManifest.yaml'
//			namespace pipelineCommon.resolveNamespaceByBranchName()
/**		yaml """
.................
"""*/
			yaml jenkinsSlavePodManifestResourceAsString
		}
	}
	options { 
		timestamps()
		
		buildDiscarder(logRotator(numToKeepStr: '25'))
	}
	parameters {
		string(name: 'TARGET_JENKINSFILE_FILE_NAME', defaultValue: 'Jenkinsfile', description: 'The desired Jenkinsfile to run')

		string(name: 'TARGET_RECKON_SCOPE', defaultValue: 'NA', description: 'The desired reckon scope to use in the build')

		string(name: 'TARGET_RECKON_STAGE', defaultValue: 'NA', description: 'The desired reckon stage to use in the build')
	}	
	environment {
		// We use this dummy environment variable to load all the properties from the designated file into environment variable (per brach)
		// This is indeed a pseudo comment 4 None
		X_EFRAT_ECHO_DUMMY_ENV_VAR = pipelineCommon.assimilateEnvironmentVariables()

		// Obtain the access token Jenkins uses to connect to GitHub (using a Jenkins credentials ID)
		GITHUB_ACCESS_TOKEN = credentials('github-demo4echo-access-token-for-reckon-gradle-plugin-id')
	}
	stages {
		stage('\u2776 setup \u2728') {//\u1F4A1
			steps {
				sh 'whoami'

				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.docker /root/.docker"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.kube /root/.kube"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.gradle/gradle.properties /root/.gradle/gradle.properties"
				sh "cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.gradle/init.gradle /root/.gradle/init.gradle"

				script {
					// Ensure target namespace is resolved
					pipelineCommon.resolveNamespaceByBranchName()
				}
			}
		}
		stage('\u2777 stamp \u2728') {//\u1F6E0
			steps {
				echo 'This will be done at the end of the build!'
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
