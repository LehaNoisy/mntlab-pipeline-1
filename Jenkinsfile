def job_name =  "${JOB_NAME}".split('/')[1]

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

def publishingResults(job_name) {
	sh 'tar -xf child1_' + number_child_job + '_dsl_do.tar.gz'
	sh 'tar -czf pipeline-alahutsin-"${BUILD_NUMBER}".tar.gz jobs.groovy Jenkinsfile build/libs/' + job_name + '.jar'
	nexusArtifactUploader artifacts: [[artifactId: 'PIPELINE', classifier: 'APP', file: 'pipeline-alahutsin-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'REL', nexusUrl: '10.6.205.119:8081/repository/test/', nexusVersion: 'nexus3', protocol: 'http', repository: 'PROD', version: '${BUILD_NUMBER}'
}

def approveProceed() {
	input 'Deploy or Abort?'
}

def deployment(job_name) { 
	tool name: 'gradle4.6', type: 'gradle'
	tool name: 'java8', type: 'jdk'
	withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin", "PATH+GROOVY_HOME=${ tool 'groovy4'}/bin"]){
		sh "groovy slave.groovy download ${BUILD_NUMBER}"
	}
	sh "tar -xvf PIPELINE-${BUILD_NUMBER}-APP.tar.gz && java -jar build/libs/" + job_name + ".jar"
}

def sendStatus(e, job_name) {
	email('failed', job_name, env.BUILD_NUMBER, env.SLAVE , e)

}

def email(status, job_name, build_number, slave_name, failed_report){
    def details = """
        STARTED: Job ${job_name} [${build_number}] + "\n"
        Runned on slave: ${slave_name} + "\n"
	Failed report: ${failed_report} + "\n"
	Status: ${status} + "\n"
        """
    emailext (
        to: 'yomivaf@uemail99.com',
        subject: "JOB:" + job_name + " " +build_number,
        body: details,
        attachLog: false
    )   
}

def sendReport(job_name) {
	email('success', job_name, env.BUILD_NUMBER, env.SLAVE, 'NONE')	
}

node("${SLAVE}") {
	try {
		stage('Preparation (Checking out)') {
			preparationCode()
		}
		stage('Building code') {
			buildingCode()
		}
		stage ('Testing code') {
			testingCode()
		}
		stage ('Triggering job and fetching artifacts') {
			fetchingArtifacts()
		}
		stage ('Packaging and Publishing results') {
			publishingResults(job_name)
		}
		stage ('Asking for manual approval') {
			approveProceed()
		}
		stage ('Deployment') {
			deployment(job_name)
		}
		stage ('Sending status') {
			sendReport(job_name)
		}
	}
	catch (Exception e) {
		sendStatus(e, job_name)
	}
}
