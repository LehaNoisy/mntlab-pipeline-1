node {
	stage('Preparation (Checking out)') {
		git branch: 'alahutsin', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
	}
	
	stage ('Building code') {
		tool name: 'gradle4.6', type: 'gradle'
		tool name: 'java8', type: 'jdk'
		withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
		    sh "gradle build"
		}
	}
	
 	stage ('Testing code'){
       	parallel(
			'Unit Tests':{
				tool name: 'gradle4.6', type: 'gradle'
				tool name: 'java8', type: 'jdk'
				withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
					sh 'gradle cucumber'
				}
			},

			'Jacoco Tests':{
				tool name: 'gradle4.6', type: 'gradle'
				tool name: 'java8', type: 'jdk'
				withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
					sh 'gradle jacocoTestReport'
				}
			},

			'Cucumber Tests':{
				tool name: 'gradle4.6', type: 'gradle'
				tool name: 'java8', type: 'jdk'
				withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
					sh 'gradle test'
				}
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
