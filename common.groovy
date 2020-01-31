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

	// If we are on the production or staging branches return the regular name ($serviceName), else return the branch namne itself
	if (namespace == "${productionBranchName}" || namespace == "${stagingBranchName}") {
		namespace = serviceName
	}
	
	return namespace
}

// Export methods by turning them into closures
ext {
	obtainCurrentBranchName = this.&obtainCurrentBranchName
	manifestVersion = this.&manifestVersion
	manifestNamespace = this.&manifestNamespace
}
