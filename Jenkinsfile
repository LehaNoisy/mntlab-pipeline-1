node {
	stage('Preparation (Checking out)') {
		git branch: 'hkavaliova', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
	}
	
	stage ('Building code') {
        sh "gradle build"
	}
}
