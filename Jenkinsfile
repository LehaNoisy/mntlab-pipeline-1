def push()
     {
          sh 'groovy push.groovy ${BUILD_NUMBER} ${WORKSPACE}'
     }
def pull()
     {
          sh 'groovy pull.groovy ${BUILD_NUMBER} ${WORKSPACE}'
     }

node ("${SLAVE}") {
     def downGradle
     def downJava    
     try {
          stage('Clean workspace before build') {
             step([$class: 'WsCleanup'])
         } 
     }
     catch(clean)
     { // ${currentBuild.fullDisplayName} show in message "Pavel Kislouski » mntlab-ci-pipeline #$BUILD_NUMBER"
          emailext body: 'Attention! Fail on step \"Clean workspace before build\"', subject: "${currentBuild.fullDisplayName} FAIL CLEAN STEP", to: 'bigmikola3@gmail.com'
          throw any
     }
    
     try {
          stage('installation') { //download java & gradle with the same names as on the master
             git url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'pkislouski'   
             downGradle = tool 'gradle4.6'
             downJava = tool 'java8'
         }
     }
     catch(installion)
     {
          emailext body: 'Attention! Fail on step \"installation\"', subject: "${currentBuild.fullDisplayName} FAIL INSTALLATION STEP", to: 'bigmikola3@gmail.com'
          throw any
     }
     
     try {
         stage('Build') {
             sh "'${downGradle}/bin/gradle' build"
         }
     }
     catch(build)
     { 
          emailext body: 'Attention! Fail on step \"build\"', subject: "${currentBuild.fullDisplayName} FAIL BUILD STEP", to: 'bigmikola3@gmail.com'
          throw any // throw any - stop pipeline if have a message
     }
     
     try {
         stage ('Testing') { // running parallel tests, can see parallel tests with a blue ocean plugin 
          parallel (
               cucumber: {
                    stage ('cucumber') {
                         sh "'${downGradle}/bin/gradle' cucumber"
                    }
               },
               jacoco: {
                    stage ('jacoco') {
                         sh "'${downGradle}/bin/gradle' jacocoTestReport"
                    }
               },
               unit: {
                    stage ('unit test') {
                         sh "'${downGradle}/bin/gradle' test"
                    }
               }
          )
         }
    }
    catch(test)
    {
         emailext body: 'Attention! Fail on step \"testing\"', subject: "${currentBuild.fullDisplayName} FAIL TESTING STEP", to: 'bigmikola3@gmail.com'
         throw any
    } 
     
    try {
         stage ('Starting child job') {
             build job: 'MNTLAB-Pavel__Kislouski-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'pkislouski']], quietPeriod: 2
         }
    }
    catch(child)
    {
         emailext body: 'Attention! Fail on step \"Starting child job\"', subject: "${currentBuild.fullDisplayName} FAIL CHILD JOB STEP", to: 'bigmikola3@gmail.com'
         throw any
    } 
  
    try {
         stage ('Copy artifact from job') { // copy artifact with plugin
             step ([$class: 'CopyArtifact',
                    projectName: 'MNTLAB-Pavel__Kislouski-child1-build-job']);
         }
     }
     catch(copy)
     {
          emailext body: 'Attention! Fail on step \"Copy artifact from job\"', subject: "${currentBuild.fullDisplayName} FAIL COPY ARTIFACT STEP", to: 'bigmikola3@gmail.com'
          throw any
     } 
    
    try {
        stage ('Unarchive & Archive') {
            sh 'cp build/libs/mntlab-ci-pipeline.jar .'
            sh 'tar -xvf child1-*.tar.gz'
            sh 'tar -czf ${WORKSPACE}/pipeline-pkislouski-${BUILD_NUMBER}.tar.gz mntlab-ci-pipeline.jar jobs.groovy'
        }
    }
    catch(archive)
    {
         emailext body: 'Attention! Fail on step \"Unarchive and Archive\"', subject: "${currentBuild.fullDisplayName} FAIL Unarchive and Archive", to: 'bigmikola3@gmail.com'
         throw any
    } 
    
    try { 
        stage ('push nexus') { // call function push
             push()
        }
    }
    catch (push)
    {
         emailext body: 'Attention! Fail on step \"PUSH\"', subject: "${currentBuild.fullDisplayName} FAIL PUSH STEP", to: 'bigmikola3@gmail.com'
         throw any
    }
     
    try {
         stage('Asking for manual approval'){
             input 'Deploy'
         }
    }
    catch (approval)
    {
         emailext body: 'Attention! Fail on step \"APPROVAL\"', subject: "${currentBuild.fullDisplayName} FAIL APPROVAL STEP", to: 'bigmikola3@gmail.com'
         throw any
    }
     
    try {
        stage ('pull from nexus') { // call function pull
            pull()
        }
    }
    catch (pull)
    {
         emailext body: 'Attention! Fail on step \"PULL\"', subject: "${currentBuild.fullDisplayName} FAIL PULL STEP", to: 'bigmikola3@gmail.com'
         throw any
    }
     
    try {
        stage ('Unarchive & Execute') { 
            sh 'tar -xvf nexus.tar.gz'
            sh 'java -jar mntlab-ci-pipeline.jar'
            emailext body: 'Attention! Deploy SUCCESS', subject: "${currentBuild.fullDisplayName} SUCCESS", to: 'bigmikola3@gmail.com' 
            }
     }
     catch (All)
     {
          emailext body: 'Attention! Fail on step \"Execute\"', subject: "${currentBuild.fullDisplayName} FAIL EXECUTE STEP", to: 'bigmikola3@gmail.com'
          throw any
     }
}    
