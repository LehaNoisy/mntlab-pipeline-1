  def tasks = [:] 
   def downGradle
   def downJava
   def Child_Job
   
node("${SLAVE}") {
   //stage 1 && 2
   stage('Preparation') { // for display purposes
      println "${SLAVE}"
      git url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'vpeshchanka' 
      downGradle = tool 'gradle4.6'
      downJava = tool 'java8'
   }
   
   //stage 3
   stage('Build') {
       withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
		sh "gradle build"
        }
   }
   
   stage ('Test') {
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
      //stage 5
   stage('Child Job')
   {
       Child_Job = build job: 'MNTLAB-vpeshchanka-child1-build-job', parameters: [string(name: 'BRANCHES', value: 'vpeshchanka')], propagate: true, wait: true
   }
   //stage 6
   stage('Add need files')
   {
       //sh "cp /var/lib/jenkins/workspace/MNTLAB-vpeshchanka-child1-build-job/vpeshchanka_dsl_script.tar.gz /var/lib/jenkins/workspace/example_2/ && echo 'All is copied!'"
       step ([$class: 'CopyArtifact',
                projectName: 'MNTLAB-vpeshchanka-child1-build-job']);
       sh "tar -xvf vpeshchanka_dsl_script.tar.gz"
       //sh "java -jar build/libs/example_2.jar > pipeline_output.log"
	   sh "tar -cf ${WORKSPACE}/pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz jobs.groovy log.txt build/libs/mntlab-ci-pipeline.jar"
   }
   stage("Push_Nexus")
   {
	   sh 'groovy script.groovy ${BUILD_NUMBER} ${WORKSPACE}'
    //nexusArtifactUploader artifacts: [[artifactId: 'PIPELINE', classifier: 'APP', file: 'pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'pipeline-vpeshchanka', nexusUrl: '10.6.204.96:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'new_repo', version: '${BUILD_NUMBER}'
   }
   stage("approve")
   {
       input 'Deploy or Abort?'
   }
   stage("Pull_Nexus")
   {
	   sh 'groovy pull_script.groovy ${BUILD_NUMBER} ${WORKSPACE}'
   }
   stage("deploy")
   {
       sh "rm -rf build"
       sh "tar -xvf nexus.tar.gz"
       sh "java -jar build/libs/example_2.jar"
   }
   stage('Results') {
      archive 'target/*.jar'
   }
}

