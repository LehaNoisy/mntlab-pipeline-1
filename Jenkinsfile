def test = ["cucumber" : {gradle("cucumber")}, "jacocoTestReport": {gradle("jacocoTestReport")}, "test": {gradle("test")}]
def STUDENT_NAME = "kklimov"
def JOB_NAME = "MNTLAB-${STUDENT_NAME}-child1-build-job"
def GITHUB_REPOSITORY = "https://github.com/MNT-Lab/mntlab-pipeline"
def gradle(c) {
    return withEnv(["JAVA_HOME=${ tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
        sh "gradle ${c}"
    }
}
def artfname = "pipeline-${STUDENT_NAME}-${BUILD_NUMBER}.tar.gz"
StageName = ""
def SendEmail(status){
    def build = currentBuild.rawBuild
    def log = currentBuild.rawBuild.getLog(20).join('\n\t\t')
    def ConsoleOutputURL = new URL("${env.BUILD_URL}consoleText")
    //Date: ${currentBuild.rawBuild.getTimestampString()}
    //Duration : ${currentBuild.rawBuild.getDurationString()}
    def EmailSubject = "'${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${status}'"
    //def Cause = "Cause: ${build.getCauses()}"
    def MailBody = """Project: ${env.JOB_NAME}
        Stage: ${StageName}
        Runned on slave: ${env.SLAVE}
        Console output at: ${ConsoleOutputURL}
        Last 20 lines in log:
        ${log}"""
    emailext (
        to: 'klimovkostya5@gmail.com',
        subject: EmailSubject,
        body: MailBody,
        attachLog: true
    )    
}


node("${SLAVE}") {
 try{
  stage ("Preparation (Checking out)"){
      StageName = "Preparation (Checking out)"
      cleanWs()
      echo "Git branch Clone"
      //git branch: STUDENT_NAME, url: GITHUB_REPOSITORY
      checkout scm
  }
  stage ("Building code") {
      StageName = "Building code"
      tool name: 'gradle4.6', type: 'gradle'
      tool name: 'java8', type: 'jdk'
      gradle("build")
  }
  stage ("Testing code") {
      StageName = "Testing code"
      parallel test
  }
  stage ("Triggering job and fetching artefact after finishing"){
      StageName = "Triggering job and fetching artefact after finishing"
      build job: JOB_NAME, parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: STUDENT_NAME]]
      copyArtifacts filter: '*.tar.gz', fingerprintArtifacts: true, projectName: JOB_NAME, selector: lastSuccessful()
  }
  stage ("Packaging and Publishing results"){
      StageName = "Packaging and Publishing results"
      sh """ tar -xvf *tar.gz
             tar -czf ${artfname} jobs.groovy Jenkinsfile  output.txt -C build/libs/ \$JOB_BASE_NAME.jar"""
      sh "groovy nexus.groovy push ${BUILD_NUMBER} ${artfname}"
      archiveArtifacts "${artfname}"
  }
  stage ("Asking for manual approval"){
      StageName = "Asking for manual approval"
      input 'Deploy?'
  }
  stage ("Deployment"){
      StageName = "Deployment"
      sh "groovy nexus.groovy pull ${BUILD_NUMBER} ${artfname}"
      sh "tar -xvf pulled*.tar.gz && java -jar *.jar" 
  }
 }
 catch(all){
    currentBuild.result = 'FAILED'
 }
 finally{  
    SendEmail(currentBuild.result)
 }
}
