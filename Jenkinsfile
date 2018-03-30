def push()  {
    
    //def ART_NAME= "pipeline-amatiev-${BUILD_NUMBER}.tar.gz"
    def ART_NAME= "aa.txt"
    def MY_CREDS = "nexus:nexus"
    def MY_REPO = "my_repo"
    def NEXUS_PATH = "EPBYMINW2472.minsk.epam.com:8081"
    def GROUP_ID = ART_NAME.substring(0, ART_NAME.indexOf("-"))
    def ART_ID = ART_NAME.substring(ART_NAME.indexOf("-")+1, ART_NAME.lastIndexOf("-"))
    def VER = ART_NAME.substring(ART_NAME.lastIndexOf("-")+1, ART_NAME.indexOf("."))
    def FILE_NAME = ART_NAME.substring(ART_NAME.indexOf("-")+1, ART_NAME.lastIndexOf("-"))
    def CONVERTED_CREDS = "${MY_CREDS}".getBytes().encodeBase64().toString()
 
    def fle = new File ("${WORKSPACE}/$ART_NAME").getBytes()
    def CONNECTION = new URL("http://${NEXUS_PATH}/repository/${MY_REPO}/${GROUP_ID}/${ART_ID}/${VER}/$ART_NAME").openConnection()
    CONNECTION.setRequestProperty("Authorization" , "Basic ${CONVERTED_CREDS}")
    CONNECTION.setRequestMethod("PUT")
    CONNECTION.doOutput = true
    //CONNECTION.setRequestProperty( "Content-Type", "application/x-gzip" )
    def writer = new DataOutputStream(CONNECTION.outputStream)
    writer.write(fle)
    writer.close()
    println CONNECTION.responseCode
    }
   
def pull()  {
    
    def ART_NAME= "pipeline-amatiev-${BUILD_NUMBER}.tar.gz"
    def MY_CREDS = "nexus:nexus"
    def MY_REPO = "my_repo"
    def NEXUS_PATH = "EPBYMINW2472.minsk.epam.com:8081"
    def GROUP_ID = ART_NAME.substring(0, ART_NAME.indexOf("-"))
    def ART_ID = ART_NAME.substring(ART_NAME.indexOf("-")+1, ART_NAME.lastIndexOf("-"))
    def VER = ART_NAME.substring(ART_NAME.lastIndexOf("-")+1, ART_NAME.indexOf("."))
    def FILE_NAME = ART_NAME.substring(ART_NAME.indexOf("-")+1, ART_NAME.lastIndexOf("-"))
    def CONVERTED_CREDS = "${MY_CREDS}".getBytes().encodeBase64().toString()


   def fl = new File("${WORKSPACE}/$ART_NAME")
        def CONNECTION = new URL("http://${NEXUS_PATH}/repository/${MY_REPO}/${GROUP_ID}/${ART_ID}/${VER}/$ART_NAME").openConnection()
        CONNECTION.setRequestProperty("Authorization" , "Basic ${CONVERTED_CREDS}")
        fl << CONNECTION.inputStream
            println CONNECTION.responseCode
    
}


def fgradle(String command){
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){sh "gradle ${command}"}
}

node("${SLAVE}") { 
    stage ('Building code'){
        checkout scm
        sh '''rm -rf *tar.gz'''
        fgradle('build')
       
    }
    stage ('Testing'){
        
         parallel(
                'Cucumber Tests': {
                    fgradle('cucumber')
                },
                'Jacoco Tests': {
                    fgradle('jacocoTestReport')
                },
                'JUnit Tests': {
                    fgradle('test')
                }
)
    }
    stage ('Triggering job and fetching'){
            
        copyArtifacts filter: '*tar.gz', projectName: 'MNTLAB-amatiev-child1-build-job', selector: lastSuccessful()
         build job: 'MNTLAB-amatiev-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: 'amatiev']]
    }
    stage ('Packaging and Publishing results'){
        sh '''
        pwd
        ls -la
        tar -xzf *tar.gz
            cat output.txt
            pwd
            ls -la
            ls -la ./build/libs
            tar -czf pipeline-amatiev-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C ./build/libs mntlab-ci-pipeline.jar
          touch aaa.txt
          pwd
          ls -la
          '''
   
        push()
    }
    stage ('Asking for manual approval'){
        timeout(time: 20, unit: 'SECONDS') {
     input message: 'Asking for deploy approval', ok: 'Deploy'
         }
       
        sh'''rm -rf *tar.gz'''
        
       
    }
    stage ('Deployment'){
        pull()
        sh '''rm -rf *.jar
        tar -xzf *tar.gz
        java -jar *.jar'''
    }
    stage ('Sending status'){
       
    }
}

