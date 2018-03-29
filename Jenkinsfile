node("${SLAVE}") {
	stage('Preparation (Checking out)') {
		git branch: 'ashumilau', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
	}
	
	stage ('Building code') {
        sh "gradle build"
	}
}
