node("${SLAVE}") { 
    echo "Hello MNT-Lab"
    stage ('Preparation (Checking out)'){
        sh "echo Git branch Clone"
        git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    }
    stage ('Building code') {
        sh "echo Starting Build"
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle clean build"
        }
    }
    stage ('Testing code'){
    
    }
    stage ('Triggering job and fetching artefact after finishing'){
    
    }
    stage ('Packaging and Publishing results'){
    
    }
    stage ('Asking for manual approval'){
    
    }
    stage ('Deployment'){
    
    }
}
