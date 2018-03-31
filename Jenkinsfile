def NexusPull() {
    withEnv(["PATH+GROOVY_HOME=${tool 'groovy4'}/bin"])
        {sh 'groovy pullme.groovy $BUILD_NUMBER'}    
    }
def NexusPush() {
        archiveArtifacts 'pipeline-hkavaliova-$BUILD_NUMBER.tar.gz'
        nexusArtifactUploader artifacts: 
        [[artifactId: 'AKOart-pipeline', 
        classifier: '', 
        file: 'pipeline-hkavaliova-$BUILD_NUMBER.tar.gz', 
        type: 'tar.gz']], 
        //credentialsId: '32eaa40f-beb1-4af6-a21d-3dffaf6abfe6',
        credentialsId: 'nexus-creds',
        groupId: 'PipelineGroup', 
        nexusUrl: 'epbyminw7423.minsk.epam.com:8081', 
        nexusVersion: 'nexus3', 
        protocol: 'http', 
        repository: 'AKO-maven2-hosted-repo', 
        version: '${BUILD_NUMBER}'   
    }
def EmailOut(stage_name){
    emailext attachLog: true, 
    body: "Job failed at ${stage_name} stage, see attached log for more info", 
    subject: 'FAILURE', 
    to: 'Hanna_Kavaliova@epam.com'
    }
def SuccessNotification(){
    emailext attachLog: true, 
    body: "Job is successful", 
    subject: 'SUCCESS', 
    to: 'Hanna_Kavaliova@epam.com'
    }

node ("${SLAVE}"){
//node {
	
	try {
	    stage('Preparation (Checking out)') {
                checkout scm
		    //git branch: 'hkavaliova', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
	        //def GRADLEPATH = tool 'gradle 4.6'
	        //env.PATH = env.PATH + ":'${GRADLEPATH}/bin'"
	        //def JAVAPATH = tool 'OracleJDKv8-161'
	    }
	} 
	catch (all){
        EmailOut('Preparation (Checking out)')
        throw any
    }
	
	try {
	    stage ('Building code') {
        
	        tool name: 'gradle4.6', type: 'gradle'
	        tool name: 'java8', type: 'jdk'
	        tool name: 'groovy4', type: 'hudson.plugins.groovy.GroovyInstallation'
	        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"])
	
	        {sh "gradle build"}
	    }
	}
	catch (all){
        EmailOut('Building code')
        throw any
    }
	
	try {
	    stage ('Testing code'){
	    
	        tool name: 'gradle4.6', type: 'gradle'
	        tool name: 'java8', type: 'jdk'
	        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"])
       	    {parallel(
			    CUCUMBER:{sh 'gradle cucumber'},
                JACOCO:{sh 'gradle jacocoTestReport'},
			    UNIT:{sh 'gradle test'}
		    )}
        } 
	}
	catch (all){
        EmailOut('Testing code')
        throw any
    }
	
    try {
        stage ('Fetching Child job1'){
        
            build job: 'MNTLAB-hkavaliova-child1-build-job', 
            parameters: 
            [[$class: 'com.cwctravel.hudson.plugins.extended_choice_parameter.ExtendedChoiceParameterValue', 
            name: 'selbran', 
            value: 'hkavaliova']], 
            propagate: false
        }
    }
    catch (all){
        EmailOut('Fetching Child job1')
        throw any
    }
    
    try {
        stage ('Repackage for Nexus'){
        
            //sh "tar -xvzf archive-${BUILD_TAG}.tar.gz -C /opt/jenkins/master/artefacts/ "
            sh 'rm -rf *.tar.gz'
            copyArtifacts filter: '*tar.gz', projectName: 'MNTLAB-hkavaliova-child1-build-job'
            //target: '${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}'
            sh 'tar -xzvf *.tar.gz'
            sh 'tar -czvf pipeline-hkavaliova-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C build/libs/ $JOB_NAME.jar'
            NexusPush()
        }
    }
    catch (all){
        EmailOut('Repackage for Nexus')
        throw any
    }
    
    try {
        stage('Approval'){
        
            input message: 'Do You Approve this Deployment?', ok: 'YES'
        }
    }
    catch (all){
        EmailOut('Approval')
        throw any
    }
    
    try {
        stage('Download Artifact'){
        
            NexusPull()
            //curl -X GET -u ako:ako "http://EPBYMINW7423.minsk.epam.com:8081/repository/AKO-maven2-hosted-repo/PipelineGroup/AKOart-pipeline/37/AKOart-pipeline-37.tar.gz"
            //sh "curl -v --user 'ako:ako' --upload-file ./pipeline-hkavaliova-$BUILD_NUMBER.tar.gz http://EPBYMINW7423.minsk.epam.com:8081/repository/AKO-maven2-hosted-repo/PipelineGroup/AKOart-pipeline/$BUILD_NUMBER/AKOart-pipeline-$BUILD_NUMBER.tar.gz"
        }
    }
    catch (all){
        EmailOut('Download Artifact')
        throw any
    }
    
    try {
        stage ('Deploy'){
            sh 'tar -xvzf pipeline-hkavaliova-$BUILD_NUMBER.tar.gz'
            sh 'java -jar *.jar'
            //sh 'tar -xvzf *.tar.gz'
            //sh "java -jar build/libs/PipelineJob.jar"
        }
    }
    catch (all){
        EmailOut('Deploy')
        throw any
    }
    finally {SuccessNotification()}
}
