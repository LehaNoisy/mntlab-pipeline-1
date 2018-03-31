  def tasks = [:] 
   def downGradle
   def downJava
   def Child_Job
   def target_arch = "deploy_app.tar.gz"
node("${SLAVE}") {
   //stage 1 && 2
   try{           
	   stage('Preparation') { // for display purposes
	      println "${SLAVE}"
	      git url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'vpeshchanka' 
	      downGradle = tool 'gradle4.6'
	      downJava = tool 'java8'
	   }
	}
   catch(exception)
   {
       emailext body: 'Attention! Fail on step Preparation or cannot download from git', subject: 'mntlab-ci-pipeline - FAIL \"PREPARATION STEP\"', to: 'valera.peshchenko@gmail.com'
	throw any
   }
   
   //stage 3
   try
   {	 
	   stage('Build') {
	       checkout scm
	       sh 'rm -rf *tar.gz'
	       withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
			sh "gradle build"
		}
	   }
   }
   catch(exception)
   {
	   emailext body: 'Attention! Fail on step \"BUILD\"', subject: 'mntlab-ci-pipeline - \"FAIL BUILD!\"', to: 'valera.peshchenko@gmail.com'
	   throw any
   }
   try
   {
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
   }
	catch(exception)
	{
		emailext body: 'Attention! Fail on step Test of application', subject: 'mntlab-ci-pipeline - \"FAIL TEST!\"', to: 'valera.peshchenko@gmail.com'
		throw any
	}
      //stage 5
   try
   {
   stage('Child Job')
   {
       Child_Job = build job: 'MNTLAB-vpeshchanka-child1-build-job', parameters: [string(name: 'BRANCHES', value: 'vpeshchanka')], propagate: true, wait: true
   }
   }
   catch(exception)
   {
	emailext body: 'Attention! Fail on step Child Job. Emerged some errors, when step \"CHILD JOB\" was running', subject: 'mntlab-ci-pipeline - FAIL Child Job!', to: 'valera.peshchenko@gmail.com'	
	throw any
   }
   //stage 6
   try
   {
	   stage('Form Artefact')
	   {
	       //sh "cp /var/lib/jenkins/workspace/MNTLAB-vpeshchanka-child1-build-job/vpeshchanka_dsl_script.tar.gz /var/lib/jenkins/workspace/example_2/ && echo 'All is copied!'"
	       step ([$class: 'CopyArtifact',
			projectName: 'MNTLAB-vpeshchanka-child1-build-job']);
	       sh "tar -xvf vpeshchanka_dsl_script.tar.gz"
	       //sh "java -jar build/libs/example_2.jar > pipeline_output.log"
		   sh "tar -cf ${WORKSPACE}/pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz jobs.groovy log.txt build/libs/mntlab-ci-pipeline.jar"
	   }
   }
   catch(exception)
   {
	emailext body: 'Attention! Fail on step \"Form Artefact\"', subject: 'mntlab-ci-pipeline - FAIL \"FORM ARTEFACT\"!', to: 'valera.peshchenko@gmail.com'	
	throw any
   }
   try
   {
	   stage("Push_Nexus")
	   {
		   sh 'groovy script.groovy ${BUILD_NUMBER} ${WORKSPACE}'
	   }
   }
   catch(exception)
   {
	     emailext body: 'Attention! Fail on step \"Push_Nexus\"', subject: 'mntlab-ci-pipeline - FAIL \"PUSH NEXUS\"!', to: 'valera.peshchenko@gmail.com'
	   throw any
   }
	
   try 
   {
	   stage("approve")
	   {
	       input 'Deploy or Abort?'
	   }
   }
   catch(exception)
   {
	     emailext body: 'Abort Deploy application!', subject: 'mntlab-ci-pipeline - \"ABORT DEPLOY APPLICATION\"', to: 'valera.peshchenko@gmail.com'
	   throw any
   }
	
   try
   {
	   stage("Pull_Nexus")
	   {
		   sh 'groovy pull_script.groovy ${BUILD_NUMBER} ${WORKSPACE} deploy_app.tar.gz'
	   }
   }
   catch(exception)
   {
		emailext body: 'Attention! Fail on step Pull_Nexus', subject: 'mntlab-ci-pipeline - FAIL \"PULL NEXUS\"', to: 'valera.peshchenko@gmail.com'
	   throw any
   }
   try
   {
	   stage("deploy")
	   {
	       sh "rm -rf build"
		   sh "tar -xvf ${target_arch}"
	       sh "java -jar build/libs/mntlab-ci-pipeline.jar"
	       emailext body: 'Deploy has done successfully!', subject: 'mntlab-ci-pipeline', to: 'valera.peshchenko@gmail.com'
	   }
   }
   catch(exception)
   {
		emailext body: 'Attention! Fail on step \"Deploy application\"', subject: 'mntlab-ci-pipeline - FAIL \"DEPLOY APPLICATION\"', to: 'valera.peshchenko@gmail.com'
	   throw any
   }
}

