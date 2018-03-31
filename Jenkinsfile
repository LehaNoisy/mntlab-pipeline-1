def GIT_URL = "https://github.com/MNT-Lab/mntlab-pipeline.git"
def STUDENT = "nbuzin"
def GRADLE = "gradle4.6"
def JDK = "java8"
   
node("${SLAVE}"){ 
   
   try{
      stage("Preparation") {
         cleanWs()
         echo "Preparation - repo cloning"
         git branch: "${STUDENT}", url: "${GIT_URL}" }
   }
   catch(exception){
      emailext attachLog: true, body:""" JOB_NAME="${env.JOB_NAME}" --- ADDITIONAL INFORMATION YOU CAN LOOK IN ATTACHED LOG"""
   }
      
      try{
         stage ("Building code") {
            echo "Starting Build"
            tool name: "${GRADLE}", type: "gradle"
            tool name: "${JDK}", type: "jdk"
            withEnv(["JAVA_HOME=${ tool "${JDK}" }", "PATH+GRADLE=${tool "${GRADLE}"}/bin"]){
            sh "gradle build"
            echo "Finishing Build"}
       }
       catch (exception){
          emailext attachLog: true, body:""" JOB_NAME="${env.JOB_NAME}" --- ADDITIONAL INFORMATION YOU CAN LOOK IN ATTACHED LOG"""
       }
          
     try{
         stage("Testing") {
            echo "Starting Tests"
            parallel(
                     "Cucumber Tests": {sh "${tool "${GRADLE}"}/bin/gradle cucumber"},
                     "Unit Tests": {sh "${tool "${GRADLE}"}/bin/gradle test"},
                     "Jacoco Tests": {sh "${tool "${GRADLE}"}/bin/gradle jacocoTestReport"}
                     )
            echo "Finishing Tests"}
    }
   catch (exception){
         emailext attachLog: true, body:""" JOB_NAME="${env.JOB_NAME}" --- ADDITIONAL INFORMATION YOU CAN LOOK IN ATTACHED LOG"""      
   }
         
   try{
            stage("Triggering job and fetching artefact after finishing") {
               build job: "MNTLAB-${STUDENT}-child1-build-job", parameters: [string(name: "BRANCH_NAME", value: "${STUDENT}")]
        step ([$class: "CopyArtifact",
               projectName: "MNTLAB-${STUDENT}-child1-build-job",
               filter: "*.tar.gz"])
    }
    catch (exception){
         emailext attachLog: true, body:""" JOB_NAME="${env.JOB_NAME}" --- ADDITIONAL INFORMATION YOU CAN LOOK IN ATTACHED LOG""" 
    }
      
    stage("Packaging and Publishing results") {
       sh "tar -xvf *.tar.gz"
       sh "cp build/libs/mntlab-ci-pipeline.jar ."
       sh "tar -czf pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz mntlab-ci-pipeline.jar jobs.groovy Jenkinsfile"
       sh "groovy push.groovy ${BUILD_NUMBER}"
       sh "rm -rf *.tar.gz"
       sh "rm -rf *.jar"
    }  
   
    stage("Asking for manual approval") {
      input "Do you want to Deploy?"
    }   
    
    stage("Deployment") {
       sh "groovy pull.groovy ${BUILD_NUMBER}"
       sh "tar -xvf *.tar.gz"
       sh "java -jar *.jar"   
    }
   
     stage ("Email notification") {
        emailext attachLog: true, body: 
           """ JOB_NAME="${env.JOB_NAME}" 
           Created archive:
           ARCHIVE_NAME=pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz ;
           BUILD_NUMBER=${BUILD_NUMBER} 
           ADDITIONAL INFORMATION YOU CAN LOOK IN ATTACHED LOG""",
           subject: "Jenkins-job", to: "nikbuzin97@gmail.com"
    }
   
   
}
