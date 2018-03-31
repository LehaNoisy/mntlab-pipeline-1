def student = "ashumilau"

node(env.SLAVE){
        def downGradle
        def downJava
	
try{
     stage ('Checking out') {
     git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
     downGradle = tool 'gradle4.6' 
     downJava = tool 'java8'
     withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
     sh 'gradle build'}}
    }
catch(exception)
   	{
       	   emailext body: 'Fail!', subject: 'Checking out fail!"', to: 'shumilovy@mail.ru'
	   throw any
	}
	
        
   stage('Results') {
      archive 'target/*.jar'
   	}
	
try{
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
    		})}} 
}
catch(exception)
   {
      emailext body: 'Fail!', subject: 'Tests fail!"', to: 'shumilovy@mail.ru'
      throw any
   }
   
     try{	
     stage("Triggering job and fetching artefact after finishing") {
        sh 'rm -rf *.tar.gz'
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: "BRANCH_NAME", value: "${student}")]
        step ([$class: "CopyArtifact",
               projectName: "MNTLAB-${student}-child1-build-job",
               filter: "*.tar.gz"])
    }
     }
    catch(exception)
   {
      emailext body: 'Fail!', subject: 'Triggering fail!"', to: 'shumilovy@mail.ru'
      throw any
    }     
	     
    try{     
    stage ('Packaging and Publishing results') {
        sh 'tar xvf *.tar.gz' 	
        sh 'tar -czf pipeline-ashumilau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-ashumilau-${BUILD_NUMBER}.tar.gz'
        sh "groovy push.groovy ${BUILD_NUMBER}"
        //Delete tar from Workspace
        sh "rm -rf *.tar.gz"
        sh "rm -rf *.jar"
    }
    }
    catch(exception)
    {
      emailext body: 'Fail!', subject: 'Packaging and Publishing fail!"', to: 'shumilovy@mail.ru'
      throw any
    }    
    
    stage("Asking for manual approval") {
      input "Do you want to Deploy?"
    } 
    
    try{
    stage ('Deploy (Pull)') {
     sh "groovy pull.groovy ${BUILD_NUMBER}"
     sh "tar -xvf *.tar.gz"
     sh "java -jar *.jar" 
    }
    }
    catch(exception)
    {
      emailext body: 'Fail!', subject: 'Deploy (Pull) fail!"', to: 'shumilovy@mail.ru'
      throw any
    }   
	    
    stage ('Email notification') {
        emailext body: 'Congratulations, all perfectly! JOB_NAME=pipeline-ashumilau-${BUILD_NUMBER}.tar.gz BUILD_NUMBER=${BUILD_NUMBER}', subject: 'Message', to: 'shumilovy@mail.ru'
    }
}
