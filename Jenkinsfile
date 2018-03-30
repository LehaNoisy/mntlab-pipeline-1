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



node("${SLAVE}") {
  echo "Hello MNT-Lab"
  stage ("Preparation (Checking out)"){
      cleanWs()
      echo "Git branch Clone"
      git branch: STUDENT_NAME, url: GITHUB_REPOSITORY
  }
  stage ("Building code") {
      echo "Starting Build section"
      tool name: 'gradle4.6', type: 'gradle'
      tool name: 'java8', type: 'jdk'
      gradle("build")
      echo "Builded"
  }
  stage ("Testing code") {
    parallel test
  }
  stage ('Triggering job and fetching artefact after finishing'){
    build job: JOB_NAME, parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: STUDENT_NAME]]
    copyArtifacts filter: '*.tar.gz', fingerprintArtifacts: true, projectName: JOB_NAME, selector: lastSuccessful()
  }
  stage ('Packaging and Publishing results'){
    sh """ tar -xvf *tar.gz
           tar -czf ${artfname} jobs.groovy Jenkinsfile  output.txt -C build/libs/ \$JOB_BASE_NAME.jar"""
    sh "groovy nexus.groovy push ${BUILD_NUMBER} ${artfname}"
    archiveArtifacts "${artfname}"
  }
  stage ('Asking for manual approval'){
    input 'Deploy?'
  }
  stage ('Deployment'){
    sh "groovy nexus.groovy pull ${BUILD_NUMBER} ${artfname}"
  }
}
