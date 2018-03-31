def student = "azaitsau"

node("${SLAVE}"){
    cleanWs()
    echo "Install java & gradle"
    def downGradle
    def downJava
//check	fails >> to gmail
    try{
        stage ('Checking out') {
            git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            downGradle = tool 'gradle4.6' 
            downJava = tool 'java8'
        }
    } 
    catch(exception)
        {
        emailext body: 'Warning! Checking out to git was faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail	
    try{ 
        stage('Build') {
        echo "Build is starting"
        if (isUnix()) {
            sh "'${downGradle}/bin/gradle' build"
        } else {
            bat(/"${downGradle}\bin\gradle" build/)
            }
        }
    }
    catch(exception)
        {
        emailext body: 'Warning! Build was faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{ 
        stage('Results') {
            archive 'target/*.jar'
        }
    }
    catch(exception)
        {
        emailext body: 'Warning! Archive was faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{ 
	stage ('Testing') {
        echo "Tests are starting"
    	parallel (
    	    cucumber: {
    	        stage ('cucumber') {
    		    sh "'${downGradle}/bin/gradle' cucumber"
    		}
    	    },
    	    jacoco: {
    		stage ('jacoco') {
    		    sh "'${downGradle}/bin/gradle' jacocoTestReport"
    		}
    	    },
    	    unit: {
    		stage ('unit test') {
    		    sh "'${downGradle}/bin/gradle' test"
    		}
    	    }
    	)
	}
    }
    catch(exception)
        {
        emailext body: 'Warning! Tests were faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{ 
        stage("Triggering job and fetching artefact after finishing") {
            sh 'rm -rf *.tar.gz'
            build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: "ChooseBranch", value: "${student}")]
            step ([$class: "CopyArtifact",
                projectName: "MNTLAB-${student}-child1-build-job",
                filter: "*.tar.gz"])
        }
    }
    catch(exception)
        {
        emailext body: 'Warning! Tests were faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{ 
        stage ('Packaging and Publishing results') {
            sh 'tar xvf *.tar.gz' 	
            sh 'tar -czf pipeline-azaitsau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
            archiveArtifacts 'pipeline-azaitsau-${BUILD_NUMBER}.tar.gz'
            //nexusArtifactUploader artifacts: [[artifactId: 'Pip-artifact', classifier: 'app', file: 'pipeline-azaitsau-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'MNT-pipeline', nexusUrl: '10.6.204.75:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'Realise', version: '${BUILD_NUMBER}'
            //sh "groovy nexus.groovy pull pipeline-azaitsau-\$BUILD_NUMBER.tar.gz"
            sh "groovy push.groovy ${BUILD_NUMBER}"
            sh "rm -rf *.tar.gz"
            sh "rm -rf *.jar"
        }
    }
    catch(exception)
        {
        emailext body: 'Warning! Packaging and Publishing were faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{
        stage('Asking for manual approval') {
            timeout(time: 5, unit: 'MINUTES') {
            input message: 'Do you want to release this build?', ok: 'Yes' 
            }
        }
    }
    catch(exception)
        {
        emailext body: 'Warning! manual approval was faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
//check	fails >> to gmail
    try{ 
        stage ('Deploy'){
        sh "groovy pull.groovy ${BUILD_NUMBER}"
        sh 'tar xvf *${BUILD_NUMBER}.tar.gz'
        sh 'java1 -jar mntlab-ci-pipeline.jar'
        }
    }
    catch(exception)
        {
        emailext body: 'Deploy was faild', subject: 'mntlab-ci-pipeline - FAIL', to: 'sashazaycev212@gmail.com'
	throw any
        }
}
    /*stage ('Email notification') { 
        emailext attachLog: true, body: 
           """ JOB_NAME="${env.JOB_NAME}"
               ARCHIVE_NAME=pipeline-${student}-${BUILD_NUMBER}.tar.gz ;
               BUILD_NUMBER=${BUILD_NUMBER} """,
               subject: "Jenkins-job", to: 'sashazaycev212@gmail.com'
}*/
    


