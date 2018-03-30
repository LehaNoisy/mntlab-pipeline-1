//@NonCPS
//def (slave_name, job_name) =  ${JOB_NAME}.split('/')
//echo slave_name
//echo job_name
echo ${JOB_NAME}
node("${SLAVE}") {
	def child_job = 0
    	def number_child_job = 0
	
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
		child_job = build job: 'MNTLAB-alahutsin-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'alahutsin')], wait: true
		number_child_job = child_job.getNumber()
		step([$class: 'CopyArtifact', projectName: 'MNTLAB-alahutsin-child1-build-job', filter: '*.tar.gz']);
		/*
		wrap([$class: 'TimestamperBuildWrapper']) {
			echo "stage: 'Triggering job and fetching artifacts' id done!"
    }
    */
     
	stage ('Packaging and Publishing results'){
		sh 'tar -xf child1_' + number_child_job + '_dsl_do.tar.gz'
		//sh 'pwd && echo ${JOB_NAME}'
		//sh 'tar -czf pipeline-alahutsin-"${BUILD_NUMBER}".tar.gz jobs.groovy Jenkinsfile build/libs/${JOB_NAME}.jar'
		sh 'tar -czf pipeline-alahutsin-"${BUILD_NUMBER}".tar.gz jobs.groovy Jenkinsfile build/libs/' + job_name + '.jar'
		nexusArtifactUploader artifacts: [[artifactId: 'PIPELINE', classifier: 'APP', file: 'pipeline-alahutsin-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'b4e27ed2-dbbb-4efe-ba2e-c0952ae2d77e', groupId: 'REL', nexusUrl: 'epbyminw2467.minsk.epam.com:8081/repository/test/', nexusVersion: 'nexus3', protocol: 'http', repository: 'PROD', version: '${BUILD_NUMBER}'
    }

	stage ('Asking for manual approval'){
    		echo 'Asking for manual approval: do sometinng [in progress]'
	}

	stage ('Deployment'){
		echo 'Deployment: do sometinng [in progress]'
	}

	stage ('Sending status'){
		echo 'Sending status: do sometinng [in progress]'
	}
}
