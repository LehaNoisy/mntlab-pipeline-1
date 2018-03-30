import javax.mail.*
import javax.mail.internet.*

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

def deployment() { 
	sh 'ls && java -jar build/libs/mntlab-ci-pipeline.jar'
}

def sendStatus(e) {
	echo 'Sending status: do sometinng [in progress send bad status to email]' + ' ' + e 
	//sendMail(host, sender, receivers, subject, e)
}

def sendMail(host, sender, receivers, subject, text) {
    Properties props = System.getProperties()
    props.put("mail.smtp.ru", host)
    Session session = Session.getDefaultInstance(props, null)

    MimeMessage message = new MimeMessage(session)
    message.setFrom(new InternetAddress(sender))
    receivers.split(',').each {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(it))
    }
    message.setSubject(subject)
    message.setText(text)

    println 'Sending mail to ' + receivers + '.'
    Transport.send(message)
    println 'Mail sent.'
}

def sendReport() {
	echo 'Sending status: do sometinng [in progress send good status to email]' + ' ' 
	sendMail('mail.ru', 'jenkins@report.com', 'skyzzz@mail.ru', 'report', 'OKOKOKOKOKOKOKOK')
	
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
			deployment()
		}
		stage ('Sending status') {
			sendReport()
		}
	}
	catch (Exception e) {
		sendStatus(e)
	}
}
