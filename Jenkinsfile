
def push(String name){


    def nexus_name = "epbyminw2468.minsk.epam.com:8081"
    def repo = "repo_for_script"

    def list_out = name.split("-")
    def groupId = list_out[0]
    def artifactId = list_out[1]
    def ending = list_out[2]
    def build_number = ending[0..ending.indexOf(".")-1]
    def extension = ending[ending.indexOf(".")+1..-1]

    def name_bytes = new File ("${WORKSPACE}/${name}").getBytes()

    def url = new URL("http://" + nexus_name + "/repository/" + repo + "/" + groupId + "/" + artifactId + "/" + build_number + "/" + name)

    def credentials = "nexus:nexus"
    def encoded = credentials.bytes.encodeBase64()
    def converted = credentials.getBytes().encodeBase64().toString()

    URLConnection connect = url.openConnection()
    connect.setRequestProperty("Authorization" , "Basic ${converted}")
    connect.setRequestMethod("PUT")
    connect.doOutput = true
    connect.setRequestProperty("Content-Type", "application/x-gzip")
    def writer = new DataOutputStream(connect.outputStream)
    writer.write(name_bytes)
    writer.close()
    println url
    println connect.responseCode
}

node("${SLAVE}") { 
    stage('Hello'){
        step([$class: 'WsCleanup'])
        echo 'Hello World'
    }

    stage('Prep'){
        tool name: 'java8', type: 'jdk'
        tool name: 'gradle4.6', type: 'gradle'
        git branch: 'ifilimonau', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    }

    stage('Building'){

        withEnv(["JAVA_HOME=${tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }

    }

    stage('Testing') {
        withEnv(["JAVA_HOME=${tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            parallel(
                cucumber: {
                    sh "gradle cucumber"
                },
                jacoco: {
                    sh "gradle jacocoTestReport"
                },
                unit: {
                    sh "gradle test"
                }
            )
        }
    }
    stage ('Starting child job') {

        build job: 'MNTLAB-ifilimonau-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME2', value: 'ifilimonau']]
        step ([$class: 'CopyArtifact',
               projectName: 'MNTLAB-ifilimonau-child1-build-job',
               filter: 'jobs.groovy']);
        sh """cp build/libs/PIPELINE_LOCAL_TEST.jar gradle-simple.jar
tar czvf pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile gradle-simple.jar"""
        push("pipeline-ifilimonau-${BUILD_NUMBER}.tar.gz")
    }
    stage('Approval') {
        input('Moving on?')
    }
}
