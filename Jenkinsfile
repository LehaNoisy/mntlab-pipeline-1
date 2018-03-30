import hudson.FilePath
import jenkins.model.Jenkins

def func_gradle(String command){
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){sh "gradle ${command}"}
}

def push(myfile) {
    def authString = "YWRtaW46YWRtaW4xMjM="
    def url ="http://EPBYMINW1766.minsk.epam.com:8081/repository/artifact-repo/Pipeline/EasyHello/${BUILD_NUMBER}/pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz"
    def http = new URL(url).openConnection()
    http.doOutput = true
    http.setRequestMethod("PUT")
    http.setRequestProperty("Authorization", "Basic ${authString}")
    http.setRequestProperty("Content-Type", "application/x-gzip")
    //def path = "${WORKSPACE}/pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz";//////
    //def lalala = new FilePath(Jenkins.getInstance().getComputer('EPBYMINW1766').getChannel(),path)////
    def out = new DataOutputStream(http.outputStream)
    //println lalala.getRemote()
    //def test = new File(lalala.toString())
    out.write(myfile.getBytes())
    out.close()
    println http.responseCode
}

def pull() {
    def authString = "YWRtaW46YWRtaW4xMjM="
    def url = "http://EPBYMINW1766.minsk.epam.com:8081/repository/artifact-repo/Pipeline/EasyHello/62/pipeline-ykhodzin-62.tar.gz"
    def file = new File("${WORKSPACE}/download.tar.gz")
    def down = new URL(url).openConnection()
    down.setRequestProperty("Authorization", "Basic ${authString}")
    file << down.inputStream
}

node("${SLAVE}") {
    //def work = new Nexus(this)
    stage('Preparation (Checking out)'){
        checkout scm
    }
    stage('Building code'){
        tool name: 'java8', type: 'jdk'
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'groovy4', type: 'hudson.plugins.groovy.GroovyInstallation'
        func_gradle('build')
    }
    stage('Testing code'){
        parallel(
                'Gradle cucumber': {
                    func_gradle('cucumber')
                },
                'Gradle jacocoTest': {
                    func_gradle('jacocoTestReport')
                },
                'Gradle test': {
                    func_gradle('test')
                }
        )
    }
    stage('Triggering job and fetching artefact after finishing'){
        build job: 'MNTLAB-ykhodzin-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH', value: 'ykhodzin']]
        copyArtifacts filter: '*.tar.gz', fingerprintArtifacts: true, projectName: 'MNTLAB-ykhodzin-child1-build-job', selector: lastSuccessful()//
    }
    stage('Packaging and Publishing results'){
        sh """tar -xvf ykhodzin_dsl_script.tar.gz
        tar -czf ${WORKSPACE}/pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar
ls -la
pwd
"""
////
         withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin", "PATH+GROOVY=${tool 'groovy4'}/bin"]){sh "env"}
       // def test = readFile "pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz"
        //push(test)
       // archiveArtifacts "${WORKSPACE}/pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz"
        archiveArtifacts "${test}"
    }
    stage('Asking for manual approval'){
        input 'Confirm deploy'
    }
    stage('Deployment'){
        pull()
        sh """tar -xvf download.tar.gz
        java -jar mntlab-ci-pipeline.jar"""
    }
}

