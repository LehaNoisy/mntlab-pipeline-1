def push_to_nexus() {
    nexusArtifactUploader artifacts: [[artifactId: 'pipeline', classifier: '', file: 'pipeline-uvalchkou-$BUILD_NUMBER.tar.gz', type: 'tar.gz']], credentialsId: '6d85699f-d021-4113-917f-383883902f3e', groupId: 'pipeline', nexusUrl: 'epbyminw2471.minsk.epam.com:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'uvalchkou', version: '$BUILD_NUMBER'
}
    
def pull_from_nexus() {
    def cred = "YWRtaW46YWRtaW4xMjM="
        def filename = "pipeline-${BUILD_NUMBER}.tar.gz"
        def pull_url = new URL("http://epbyminw2471.minsk.epam.com:8081/repository/uvalchkou/pipeline/pipeline/${BUILD_NUMBER}/${filename}").openConnection()
        pull_url.setRequestProperty("Authorization", "Basic ${cred}")
        def file = new File("${WORKSPACE}/${filename}")
        file << pull_url.inputStream
        println pull_url.responseCode
}

def env(String action){
    withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh 'gradle $action'
        }
}

node("${SLAVE}") {
    tool name: 'gradle4.6', type: 'gradle'
    tool name: 'java8', type: 'jdk'
    
    stage('git') {
        checkout scm
        //git branch: 'uvalchkou', url: 'https://github.com/MNT-Lab/mntlab-pipeline/'
    }
    
    stage('build'){
        env("build")
        
    }
    
    stage('tests'){
        parallel(
            cucumber_test: {env('cucumber')},
            jacoco_test: {env('jacocoTestReport')},
            gradle_test: {env('test')}
        )
    }
    
    stage('trigger'){
        build job: 'MNTLAB-uvalchkou-child1-build-job'
    }
   
    stage('Packaging_and_Publishing'){
        sh 'rm -rf *.tar.gz'
        copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-uvalchkou-child1-build-job'
        sh 'tar xzvf *.tar.gz'
        sh 'tar czvf pipeline-uvalchkou-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-uvalchkou-$BUILD_NUMBER.tar.gz'
        sh 'ls'
        push_to_nexus()
    }
    
    stage('aprooval'){
        input message: 'Process or Abort artifact pull and deploy?', ok: 'pull_and_deploy'
    }
    
    stage('pull'){
        pull_from_nexus()
    }
    
    stage('deploy'){
        sh 'ls'
        sh 'tar xzfv pipeline-uvalchkou-${BUILD_NUMBER}.tar.gz'
        sh 'ls'
        sh 'java -jar mntlab-ci-pipeline.jar'
    }
    
    /*stage('notification'){
        emailext attachLog: true, body: '', subject: 'STARTED', to: 'tarantino459@gmail.com'
    }*/
}    
