  def tasks = [:] 
   def downGradle
   def downJava
   def Child_Job
   
node {
   //stage 1 && 2
   stage('Preparation') { // for display purposes
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
       sh "tar -cf pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz jobs.groovy log.txt build/libs/mntlab-ci-pipeline.jar"
   }
   stage("Push_Nexus")
   {
        //def  ART_NAME= "ls -t1 ${WORKSPACE}/".execute().text.split()[0]
    def  ART_NAME = "pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz"
    /*It's not correct because password send in opening view*/
    def CREDS = "valera:1234"
    def REPO = "new_repo"
    def NEXUS_PATH = "EPBYMINW6593.minsk.epam.com:8081"
    def GROUP_ID = "GROUP_PIPELINE"
    println "GROUP_ID = " + GROUP_ID
    def ART_ID = "pipeline-vpeshchanka"
    println "ART_ID=" + ART_ID
    def VER = ART_NAME.substring(ART_NAME.lastIndexOf("-")+1, ART_NAME.indexOf("."))
    println "VER = " + VER
    def SUFF = ART_NAME.substring(0, ART_NAME.lastIndexOf("-"))
    println "SUFFIX = " + SUFF
    def EXT = ART_NAME.substring(ART_NAME.indexOf(".")+1)
    def CONVERTED_CREDS = "${CREDS}".getBytes().encodeBase64().toString()
    
	println InetAddress.localHost.hostName 
        def File = new File ("${WORKSPACE}/pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz").getBytes()
        def CONNECTION = new URL("http://${NEXUS_PATH}/repository/${REPO}/${GROUP_ID}/${ART_ID}/${VER}/${SUFF}-${VER}.${EXT}").openConnection()
        println "http://${NEXUS_PATH}/repository/${REPO}/${GROUP_ID}/${ART_ID}/${VER}/${SUFF}-${VER}.${EXT}"
        CONNECTION.setRequestProperty("Authorization" , "Basic ${CONVERTED_CREDS}")
        CONNECTION.setRequestMethod("PUT")
        CONNECTION.doOutput = true
        CONNECTION.setRequestProperty( "Content-Type", "application/x-gzip" )
        def writer = new DataOutputStream(CONNECTION.outputStream)
        writer.write(File)
        writer.close()
        println "CONNECTION RESPONSE = " + CONNECTION.responseCode
    //nexusArtifactUploader artifacts: [[artifactId: 'PIPELINE', classifier: 'APP', file: 'pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'pipeline-vpeshchanka', nexusUrl: '10.6.204.96:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'new_repo', version: '${BUILD_NUMBER}'
   }
   stage("Pull_Nexus")
   {
       def CONVERTED_CREDS = "valera:1234".getBytes().encodeBase64().toString()
        def url ="http://EPBYMINW6593.minsk.epam.com:8081/repository/new_repo/GROUP_PIPELINE/pipeline-vpeshchanka/${BUILD_NUMBER}/pipeline-vpeshchanka-${BUILD_NUMBER}.tar.gz"
        def file = new File("${WORKSPACE}/nexus.tar.gz")
        def down = new URL(url).openConnection()
        down.setRequestProperty("Authorization", "Basic ${CONVERTED_CREDS}")
        file << down.inputStream
   }
   stage("approve")
   {
       input 'Deploy or Abort?'
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

