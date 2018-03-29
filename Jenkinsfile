node("${SLAVE}") { 
    echo "Hello MNT-Lab"
    stage ('Preparation (Checking out)'){
        echo "Git branch Clone"
        git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    }
    stage ('Building code') {
        echo "Starting Build"
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }
        echo "End Build"
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
