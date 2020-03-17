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
def manifestVersion() {
	def currentBranchName = obtainCurrentBranchName()
	def currentVersionName = project.version.toString() // must be done this way since reckon makes project.version non serializable
	def dockerSafeTagName = currentVersionName.replace(CONST_UNSAFE_CHARACTER_FOR_DOCKER_TAG_NAME,CONST_SAFE_CHARACTER_REPLACER_FOR_DOCKER_TAG_NAME)
	def manifestedVersion = "${insignificant_version_notation}-${currentBranchName}"

	// Check if we are to work with a designated version (tag) - in which case use it
	if (project.hasProperty(CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME) == true && project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME].trim().isBlank() == false) {
		manifestedVersion = "${project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME]}-${currentBranchName}"
	}
	// Otherwise check if the version should be significant (in which case use reckon based version (tag))
	else if (currentBranchName == production_branch_name || currentBranchName == staging_branch_name) {
		manifestedVersion = "${dockerSafeTagName}-${currentBranchName}"
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

// Obtain the applicable version name
def obtainApplicableVersionName() {
	def applicableVersionName = project.version.toString()

	// Check if we are to work with a designated version (tag), otherwise return reckon based version (tag)
	if (project.hasProperty(CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME) == true && project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME].trim().isBlank() == false) {
		applicableVersionName = project.ext[CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME]
	}

	return applicableVersionName
}

// Checks if the applicable version marks a significant version or not
def isSignificantVersion() {
	def applicableVersionName = obtainApplicableVersionName()
	def isSignificantVersion = (applicableVersionName.contains(CONST_SAFE_CHARACTER_REPLACER_FOR_DOCKER_TAG_NAME) == false && applicableVersionName.contains(CONST_UNSAFE_CHARACTER_FOR_DOCKER_TAG_NAME) == false)

	return isSignificantVersion
}

// Checks if we are running on development (feature/defect) environment/branches
def isDevelopmentEnvironment() {
	def currentBranchName = obtainCurrentBranchName()
	if (currentBranchName == production_branch_name || currentBranchName == staging_branch_name) {                 
		return false
	}
	else {
		return true
	}
}

// Constructs the image name (local-wise is in development environment and so requested, remote-wise otherwise)
def manifestImageName(Properties branchSpecificProps) {
	def publishArtifactsOnDevelopmentEnvironment = branchSpecificProps.hasProperty(publishArtifactsOnDevelopmentEnvironment) ?: false
	
	// Local centric
	if (isDevelopmentEnvironment() == true && publishArtifactsOnDevelopmentEnvironment == true) {
		return productName
	}
	// Remote centric
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
	obtainApplicableVersionName = this.&obtainApplicableVersionName
	isSignificantVersion = this.&isSignificantVersion
	isDevelopmentEnvironment = this.&isDevelopmentEnvironment
	manifestImageName = this.&manifestImageName
}
