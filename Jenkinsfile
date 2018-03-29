node("${SLAVE}") {
    tool name: 'gradle4.6', type: 'gradle'
    tool name: 'java8', type: 'jdk'
    stage('git') {
        git branch: 'uvalchkou', url: 'https://github.com/MNT-Lab/mntlab-pipeline/'
    }
    
    stage('build'){
        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle install"
        }
    }
    
    stage('tests'){
        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            parallel(
                a: {sh 'gradle cucumber'},
                b: {sh 'gradle jacocoTestReport'},
                c: {sh 'gradle test'}
            )
            
        }
    }
    
    stage('trigger'){
        build job: 'MNTLAB-uvalchkou-child1-build-job', parameters: [[$class: 'ExtendedChoiceParameterValue', name: 'branch', value: 'uvalchkou']]
    }
    
    stage('copyArtefact'){
        sh 'rm -rf *.tar.gz'
        copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-uvalchkou-child1-build-job'
        sh 'ls'
    }
    
    
}


