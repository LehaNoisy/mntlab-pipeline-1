def fgradle(String command){
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){sh "gradle ${command}"}
}

def ereport(String buildStatus, nst) {
  // build status of null means successful
  //buildStatus = buildStatus ?: 'SUCCESS'
  def subj = "${buildStatus}: Job '${BUILD_TAG}]'"
    def body = """Job ${JOB_NAME} build № ${BUILD_NUMBER} was ${buildStatus}
             last stage was ${nst}
  """
    
 // def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
 //   <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
   emailext(
                    to: 'smart.birdtrap@gmail.com',
                    attachLog: true,
                    subject: subj,
                    body: body
            )
    }

nstage = ""

node("${SLAVE}"){
    try {
    stage ('Building code'){
        nstage = env.STAGE_NAME
        checkout scm
        sh '''rm -rf *tar.gz'''
        fgradle('build')    
    }
    
    stage ('Testing'){
        nstage = env.STAGE_NAME
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
        nstage = env.STAGE_NAME
        build job: 'MNTLAB-amatiev-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: 'amatiev']]
        copyArtifacts filter: '*tar.gz', projectName: 'MNTLAB-amatiev-child1-build-job', selector: lastSuccessful()
    }
    
    stage ('Packaging and Publishing results'){
        nstage = env.STAGE_NAME
        sh '''
        tar -xzf *tar.gz
        cat output.txt
        tar -czf pipeline-amatiev-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C ./build/libs mntlab-ci-pipeline.jar
        groovy pushpull.groovy push pipeline-amatiev-${BUILD_NUMBER}.tar.gz'''  
    }
    
    stage ('Asking for manual approval'){
        nstage = env.STAGE_NAME
        timeout(time: 20, unit: 'SECONDS') {
            input message: 'Asking for deploy approval', ok: 'Deploy'}
       sh'''rm -rf *tar.gz'''
    }

    stage ('Deployment'){
        nstage = env.STAGE_NAME
        sh'''
        groovy pushpull.groovy pull pipeline-amatiev-${BUILD_NUMBER}.tar.gz
        rm -rf *.jar
        tar -xzf *tar.gz
        java -jar *.jar'''
    }

    stage ('Sending status'){
        nstage = env.STAGE_NAME
       ereport(currentBuild.currentResult, nstage)
    }
        
    }
    catch (all) {
    // If there was an exception thrown, the build failed
    ereport(currentBuild.currentResult, nstage)
    }
}

