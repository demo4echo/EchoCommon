// We can omit this one as we marked the shared library to load implicitly
@Library('EchoSharedLibrary') _

// Load shared resources
def jenkinsSlavePodManifestResourceAsString = libraryResource 'jenkinsSlavePodManifest.yaml'

pipeline {
	agent {
		kubernetes {
			cloud pipelineCommon.resolveCloudNameByBranchName()
			label pipelineCommon.constructJenkinsSlavePodAgentLabel()
			defaultContainer pipelineCommon.K8S_AGENT_DEFAULT_CONTAINER
			yaml jenkinsSlavePodManifestResourceAsString
//			namespace pipelineCommon.resolveNamespaceByBranchName()
//			yamlFile 'Jenkinsfile.JenkinsSlaveManifest.yaml'
//		   yaml """
//.................
//"""
		}
	}
	options { 
		timestamps()
		
		buildDiscarder(logRotator(numToKeepStr: pipelineCommon.OPTIONS_BUILD_DISCARDER_LOG_ROTATOR_NUM_TO_KEEP_STR))

		parallelsAlwaysFailFast()
	}
	parameters {
		choice (
			name: 'TARGET_JENKINSFILE_FILE_NAME',
			choices: [
				pipelineCommon.PARAMS_TARGET_JENKINSFILE_FILE_NAME_OPTIONS[0],
				pipelineCommon.PARAMS_TARGET_JENKINSFILE_FILE_NAME_OPTIONS[1],
				pipelineCommon.PARAMS_TARGET_JENKINSFILE_FILE_NAME_OPTIONS[2]
			],
			description: 'The desired Jenkinsfile to run'
		)
		choice (
			name: 'TARGET_RECKON_SCOPE',
			choices: [
				pipelineCommon.PARAMS_TARGET_RECKON_SCOPE_OPTIONS[0],
				pipelineCommon.PARAMS_TARGET_RECKON_SCOPE_OPTIONS[1],
				pipelineCommon.PARAMS_TARGET_RECKON_SCOPE_OPTIONS[2],
				pipelineCommon.PARAMS_TARGET_RECKON_SCOPE_OPTIONS[3]
			],
			description: 'The desired reckon scope to use in the build'
		)
		choice (
			name: 'TARGET_RECKON_STAGE',
			choices: [
				pipelineCommon.PARAMS_TARGET_RECKON_STAGE_OPTIONS[0],
				pipelineCommon.PARAMS_TARGET_RECKON_STAGE_OPTIONS[1],
				pipelineCommon.PARAMS_TARGET_RECKON_STAGE_OPTIONS[2],
				pipelineCommon.PARAMS_TARGET_RECKON_STAGE_OPTIONS[3]
			],
			description: 'The desired reckon stage to use in the build'
		)
		validatingString (
			name: 'DESIGNATED_VERSION',
			defaultValue: pipelineCommon.PARAMS_DESIGNATED_VERSION_DEFAULT_VALUE,
			regex: pipelineCommon.PARAMS_DESIGNATED_VERSION_REG_EXP,
			failedValidationMessage: "Validation of designated version failed!",
			description: """
			The desiganted (desired) version to be used.
			Notes:
			------
			1. The input given must comply with 'Semantic Versioning 2.0.0' (https://semver.org) with regards to: <MAJOR>.<MINOR>.<PATCH>
			2. The version supplied must be higher than any existing version (tag) in the target repo(s)
			3. This will void the use of the 'TARGET_RECKON_SCOPE' and 'TARGET_RECKON_STAGE' parameters!
			"""
		)
		string (
			name: 'DESIGNATED_VERSION_MESSAGE',
			defaultValue: pipelineCommon.PARAMS_DESIGNATED_VERSION_MESSAGE_DEFAULT_VALUE,
			description: 'If applicable (and only for designated version), place a message that will be attached to the designated version (e.g. a customer name)'
		)
	}	
	environment {
		// We use this dummy environment variable to load all the properties from the designated file into environment variable (per branch)
		X_EFRAT_ECHO_DUMMY_ENV_VAR = pipelineCommon.assimilateEnvironmentVariables()

		// Obtain the access token Jenkins uses to connect to GitHub (using a Jenkins credentials ID)
		// Note - the values used in the credentials() helper must be String literals
		GITHUB_ACCESS_TOKEN = credentials('github-demo4echo-access-token-for-reckon-gradle-plugin-id')
	}
	stages {
		stage('\u2776 setup \u2728') {//\u1F4A1
			steps {
				//
				// Verify this build should run (e.g. don't allow a replayed build) - this step should be the first step!
				//
				validateBuildRun "${env.BUILD_TAG}"

				sh 'echo User [`whoami`] is running within [`ps -hp $$ | awk \'{print $5}\'`] Shell on Node [$NODE_HOST_NAME_ENV_VAR]'
				sh 'echo The following script is executing: [$0]'

				sh 'echo JAVA_HOME value is: [$JAVA_HOME]'
				sh 'echo PATH value is: [$PATH]'
				sh 'echo PWD value is: [$PWD]'

				sh "mkdir -p /root/.docker && cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.setup/.docker /root"
				sh "mkdir -p /root/.kube && cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.setup/.kube /root"
				sh "mkdir -p /root/.gradle && cp -ar ./${env.COMMON_SUB_MODULE_FOLDER_NAME_ENV_VAR}/.setup/.gradle /root"

				script {
					// Ensure target namespace is resolved
					pipelineCommon.resolveNamespaceByBranchName()
				}

				//
				// Update build name and description
				//
				updateBuildInformation()

				// For Debug Only!
//				sleep 300
			}
		}
		stage('\u2777 build \u2728') {//\u1F6E0
			steps {
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					dockerBuildAndPublish"
			}
		}
		stage('\u2778 package \u2728') {//\u1F4E6
			steps {
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmPackage" // helmPackageAndPublish
			}
		}
		stage('\u2779 install \u2728') {//\u1F3F4
			when {
				environment name: 'CLOUD_NAME', value: 'development'
			}
			steps {
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmUpdate"
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmTestAndClean"
			}
		}
		stage('\u277A upgrade \u2728') {//\u1F3F4
			when {
				not {
					environment name: 'CLOUD_NAME', value: 'development'
				}
			}
			steps {
//				sh "./gradlew \
//					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
//					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
//					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
//					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
//					helmUninstall"
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmUpdate"
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmTestAndClean"
			}
		}
		stage('\u277B certify \u2728') {//\u1F321
			steps {
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					certify"
			}
		}
		stage('\u277C uninstall \u2728') {//\u1F3F3
			when {
				environment name: 'CLOUD_NAME', value: 'development'
			}
			steps {
				sh "./gradlew \
					-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
					-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
					-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
					-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
					helmUninstall"
			}
		}
		stage('\u277D cleanup \u2728') {
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

			script {
				// Development cluster (serving features and defects branches) should not allow generating tags
				if (env.CLOUD_NAME != 'development') {
					if (params.DESIGNATED_VERSION.trim().isEmpty() == true) {
						// Mark the version (done at the end, otherwise all other stages apart from the first one will get other version numbers)
						sh "./gradlew \
							-Preckon.scope=${env.JENKINS_SLAVE_K8S_RECKON_SCOPE} \
							-Preckon.stage=${env.JENKINS_SLAVE_K8S_RECKON_STAGE} \
							-Dorg.ajoberstar.grgit.auth.username=${env.GITHUB_ACCESS_TOKEN} \
							publishVersion"
					}
					else {
						// Mark the designated version (done at the end, otherwise all other stages apart from the first one will get other version numbers)
						sh "./gradlew \
							-Pdemo4echo.designatedTagName=${params.DESIGNATED_VERSION} \
							-Pdemo4echo.designatedTagMessage='${params.DESIGNATED_VERSION_MESSAGE}' \
							-Dorg.ajoberstar.grgit.auth.username=${env.GITHUB_ACCESS_TOKEN} \
							publishDesignatedVersion"
					}

					// Trigger downstream end to end functional testing (wait for it to end since its failure should fail this build as well)
					build (
						job: "Echoe2eFunctionalCertification/${env.BRANCH_NAME}",
						wait: true
					)
				}
				else {
					echo 'Skipping VCS tagging as development environment has been observed (for which tags should not be generated)'
				}
			}

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
