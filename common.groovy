//
// Works with org.ajoberstar.grgit plugin
//

// Returns the current git branch name
def obtainCurrentBranchName() {
	return grgit.branch.current.name
}

// Returns the latest tag/version from git
def obtainLatestTag() {
	return grgit.tag.list().last()
}

//
// General purpose functions
//

// Builds a proper version name
def manifestVersion(boolean concatenateBranchNameAsSuffix = true) {
	def currentBranchName = obtainCurrentBranchName()
	def versionSuffix = concatenateBranchNameAsSuffix == true ? "-${currentBranchName}" : ""
	def currentVersionName = project.version.toString() // must be done this way since reckon makes project.version non serializable
	def dockerSafeTagName = currentVersionName.replace(CONST_UNSAFE_CHARACTER_FOR_DOCKER_TAG_NAME,CONST_SAFE_CHARACTER_REPLACER_FOR_DOCKER_TAG_NAME)
	def manifestedVersion = "${insignificant_version_notation}${versionSuffix}"

	// Check if we are to work with a designated version (tag) - in which case use it
	if (project.hasProperty(CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME) == true && project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME].trim().isBlank() == false) {
		manifestedVersion = "${project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME]}${versionSuffix}"
	}

	// Check if version is significant - in which case use reckon based version (tag) (don't use else-if since we need to work on the most updated 'manifestedVersion')
	if (isSignificantVersion(manifestedVersion) == true) {
		manifestedVersion = "${dockerSafeTagName}${versionSuffix}"
	}

	return manifestedVersion
}

// Construct an applicable namespace to be used by the Helm Chart
def manifestNamespace() {
	def namespace = obtainCurrentBranchName()

	// If we are on the production or staging branches return the regular name ($serviceName), else return the branch name itself
	if (namespace == production_branch_name || namespace == staging_branch_name) {
		namespace = serviceName
	}

	return namespace
}

// Checks if the applicable version marks a significant version or not
def isSignificantVersion(String versionName) {
	def isSignificantVersion = (versionName.contains(CONST_SAFE_CHARACTER_REPLACER_FOR_DOCKER_TAG_NAME) == false && versionName.contains(CONST_UNSAFE_CHARACTER_FOR_DOCKER_TAG_NAME) == false)

	return isSignificantVersion
}

// Constructs the image name (local-wise if artifacts publishing was disabled, remote-wise otherwise)
def manifestImageName(Properties branchSpecificProps) {
	def publishArtifactsDirective = branchSpecificProps.publishArtifacts != null ? branchSpecificProps.publishArtifacts.toBoolean() : true
	// Local centric (no publishing)
	if (publishArtifactsDirective == false) {
		return productName
	}
	// Remote centric (publishing in place)
	else {
		return productRepository
	}
}

// Export constants and functions (by turning the functions into closures)
ext {
	// Constants
	CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME='demo4echo.designatedTagName'
	CONST_DESIGNATED_TAG_MESSAGE_PROJECT_PROPERTY_NAME='demo4echo.designatedTagMessage'
	CONST_BRANCH_SPECIFIC_CONFIGURATION_FILE_NAME='branchSpecificConfig.properties'
	CONST_UNSAFE_CHARACTER_FOR_DOCKER_TAG_NAME='+'
	CONST_SAFE_CHARACTER_REPLACER_FOR_DOCKER_TAG_NAME='_'

	// Functions
	obtainCurrentBranchName = this.&obtainCurrentBranchName
	obtainLatestTag = this.&obtainLatestTag
	manifestVersion = this.&manifestVersion
	manifestNamespace = this.&manifestNamespace
	isSignificantVersion = this.&isSignificantVersion
	manifestImageName = this.&manifestImageName
}
