//
// Works with org.ajoberstar.grgit plugin
//

// Returns the current git branch name
def obtainCurrentBranchName() {
	return grgit.branch.current.name
}

//
// General purpose functions
//

// Builds a proper version name
def manifestVersion() {
	def currentBranchName = obtainCurrentBranchName()
	def currentVersionName = project.version.toString() // must be done this way since reckon makes project.version non serializable
	return "${currentVersionName}-${currentBranchName}"
}

// Construct an applicable namespace to be used by the Helm Chart
def manifestNamespace() {
	def namespace = obtainCurrentBranchName()

	// If we are on the production or staging branches return the regular name ($serviceName), else return the branch name itself
	if (namespace == "${productionBranchName}" || namespace == "${stagingBranchName}") {
		namespace = serviceName
	}
	
	return namespace
}

// Export constants and functions (by turning the functions into closures)
ext {
	// Constants
	CONST_DESIGNATED_TAG_NAME_PROJECT_PROPERTY_NAME='demo4echo.designatedTagName'

	// Functions
	obtainCurrentBranchName = this.&obtainCurrentBranchName
	manifestVersion = this.&manifestVersion
	manifestNamespace = this.&manifestNamespace
}
