node("${SLAVE}") {
	stage('Preparation (Checking out)') {
		git branch: 'alahutsin', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
	}
	
	stage ('Building code') {
        sh "gradle build"
	}
	
 	stage ('Testing code'){
       	parallel(
			'Unit Tests':{
				sh 'gradle cucumber'
			},

			'Jacoco Tests':{
				sh 'gradle jacocoTestReport'
			},

			'Cucumber Tests':{
				sh 'gradle test'
			}
		)
    	}
}
