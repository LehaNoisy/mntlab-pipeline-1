import hudson.FilePath
import jenkins.model.Jenkins

def func_gradle(String command){
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){sh "gradle ${command}"}
}

def email(String status){
    status = status ?: 'SUCCESS'
    def color = 'RED'
    def colorCode = '#FF0000'
    def subject = "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = "${subject} (${env.BUILD_URL})"
    def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
    // Override default values based on build status
    if (status == 'SUCCESS') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } 
    emailext (
        to: 'mushtarda@gmail.com',
        subject: subject,
        body: details,
        attachLog: true
    )    
}

node("${SLAVE}") {
    try{
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
            tar -czf ${WORKSPACE}/pipeline-ykhodzin-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar"""
            withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin", "PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){sh "groovy nex.groovy push ${BUILD_NUMBER}"}
        }
        stage('Asking for manual approval'){
            timeout(time: 10, unit: 'SECONDS') {
                input 'Confirm deploy'}
        }
        stage('Deployment'){
            withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin", "PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){sh "groovy nex.groovy pull ${BUILD_NUMBER}"}
            sh """tar -xvf download-${BUILD_NUMBER}.tar.gz
            java -jar mntlab-ci-pipeline.jar"""
        }
    }
    catch(all){
        currentBuild.result = 'FAILED'
    }
    finally{
        email(currentBuild.result)
    }
}
