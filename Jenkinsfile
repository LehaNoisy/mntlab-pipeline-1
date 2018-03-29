node {
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
      	
    stage ('Triggering job and fetching artifacts')
		build job: 'MNTLAB-alahutsin-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'alahutsin')], wait: true
		step([$class: 'CopyArtifact', projectName: 'MNTLAB-alahutsin-child1-build-job', filter: '*.tar.gz']);
		wrap([$class: 'TimestamperBuildWrapper']) {
			echo "stage: 'Triggering job and fetching artifacts' id done!"
    }
}
