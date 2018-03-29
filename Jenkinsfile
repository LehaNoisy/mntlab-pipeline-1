def tests = [:]
tests["Unit Tests"] = {sh 'gradle test'}
tests["Jacoco Tests"] = {sh 'gradle jacocoTestReport'}
tests["Cucumber Tests"] = {sh 'gradle cucumber'}

node("${SLAVE}") {
    echo "Hello MNT-Lab"
    stage ('Preparation (Checking out)'){
        echo " Try git branch clone"
        git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        echo "Branch Clone : Done"
    }
    stage ('Building code') {
        echo "Try Build"
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }
        echo "End Build"
    }
    stage ('Testing code') {
        echo "Try Build"
        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
            parallel(tests)
        }
        echo "End Test"
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
