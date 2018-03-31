def student = "ashumilau"

node(env.SLAVE){
	try{ 
        def downGradle
        def downJava
	
        stage ('Checking out') {
        git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        downGradle = tool 'gradle4.6' 
        downJava = tool 'java8'
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
        sh 'gradle build'}}
	}
	catch(exception)
   	{
       		emailext body: 'Attention!', subject: 'Checking out fail"', to: 'shumilovy@mail.ru'
		throw any
	}
        
   stage('Results') {
      archive 'target/*.jar'
   }
   stage ('Testing') {
       withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
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
     stage("Triggering job and fetching artefact after finishing") {
        sh 'rm -rf *.tar.gz'
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: "BRANCH_NAME", value: "${student}")]
        step ([$class: "CopyArtifact",
               projectName: "MNTLAB-${student}-child1-build-job",
               filter: "*.tar.gz"])
    }
    stage ('Packaging and Publishing results') {
        sh 'tar xvf *.tar.gz' 	
        sh 'tar -czf pipeline-ashumilau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-ashumilau-${BUILD_NUMBER}.tar.gz'
        sh "groovy push.groovy ${BUILD_NUMBER}"
        //Delete tar from Workspace
        sh "rm -rf *.tar.gz"
        sh "rm -rf *.jar"
    }  
    
    stage("Asking for manual approval") {
      input "Do you want to Deploy?"
    } 
	
    stage ('Deploy (Pull)') {
     sh "groovy pull.groovy ${BUILD_NUMBER}"
     sh "tar -xvf *.tar.gz"
     sh "java -jar *.jar" 
    }
    
    stage ('Email notification') {
        emailext body: 'Hello! JOB_NAME=pipeline-ashumilau-${BUILD_NUMBER}.tar.gz BUILD_NUMBER=${BUILD_NUMBER}', subject: 'Message', to: 'shumilovy@mail.ru'
    }
}
