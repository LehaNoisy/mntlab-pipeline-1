def fgradle(String command){
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){sh "gradle ${command}"}
}

node("${SLAVE}"){
    
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
        build job: 'MNTLAB-amatiev-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: 'amatiev']]
        copyArtifacts filter: '*tar.gz', projectName: 'MNTLAB-amatiev-child1-build-job', selector: lastSuccessful()
    }
    
    stage ('Packaging and Publishing results'){
        sh '''
        tar -xzf *tar.gz
        cat output.txt
        tar -czf pipeline-amatiev-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C ./build/libs mntlab-ci-pipeline.jar
        groovy pushpull.groovy push pipeline-amatiev-${BUILD_NUMBER}.tar.gz'''  
    }
    
    stage ('Asking for manual approval'){
        timeout(time: 20, unit: 'SECONDS') {
            input message: 'Asking for deploy approval', ok: 'Deploy'}
       sh'''rm -rf *tar.gz'''
    }

    stage ('Deployment'){
        sh'''
        groovy pushpull.groovy pull pipeline-amatiev-${BUILD_NUMBER}.tar.gz
        rm -rf *.jar
        tar -xzf *tar.gz
        java -jar *.jar'''
    }

    stage ('Sending status'){
       
    }
}

