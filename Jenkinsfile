/*
@NonCPS
def slave_name =  "${JOB_NAME}".split('/')[0]
def job_name =  "${JOB_NAME}".split('/')[1]
*/

def child_job = 0
def number_child_job = 0

def preparationCode() {
	git branch: 'alahutsin', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
}

def buildingCode() {
	tool name: 'gradle4.6', type: 'gradle'
	tool name: 'java8', type: 'jdk'
	withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
		sh "gradle build"
	}
}

def testingCode() {
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

def fetchingArtifacts() {
	child_job = build job: 'MNTLAB-alahutsin-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'alahutsin')], wait: true
	number_child_job = child_job.getNumber()
	step([$class: 'CopyArtifact', projectName: 'MNTLAB-alahutsin-child1-build-job', filter: '*.tar.gz']);
}

def publishingResults() {
	sh 'tar -xf child1_' + number_child_job + '_dsl_do.tar.gz'
	sh 'tar -czf pipeline-alahutsin-"${BUILD_NUMBER}".tar.gz jobs.groovy Jenkinsfile build/libs/mntlab-ci-pipeline.jar'
	nexusArtifactUploader artifacts: [[artifactId: 'PIPELINE', classifier: 'APP', file: 'pipeline-alahutsin-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'REL', nexusUrl: '10.6.205.119:8081/repository/test/', nexusVersion: 'nexus3', protocol: 'http', repository: 'PROD', version: '${BUILD_NUMBER}'
}

node("${SLAVE}") {	
	stage('Preparation (Checking out)') {
		try {
			preparationCode()
		}
		catch (Exception e) {
			echo e
		}
	}
	
	stage('Building code') {
		try {
			buildingCode()
		}
		catch (Exception e) {
			echo e
		}
	}
	
 	stage ('Testing code'){
		try {
			testingCode()
		}
		catch (Exception e) {
			echo e
		}
	}

      	
	stage ('Triggering job and fetching artifacts'){
		try {
			fetchingArtifacts()
		}
		catch (Exception e) {
			echo e
		}		
	}

	stage ('Packaging and Publishing results'){
		try {
			publishingResults()
		}
		catch (Exception e) {
			echo e
		}		
	}

	stage ('Asking for manual approval'){
    		input 'Deploy or Abort?'
	}

	stage ('Deployment'){
		sh 'java -jar build/libs/' + job_name + '.jar'
	}

	stage ('Sending status'){
		echo 'Sending status: do sometinng [in progress]'
	}
}
