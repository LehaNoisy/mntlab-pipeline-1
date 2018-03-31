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
     try{
          stage('Clean workspace before build') {
             step([$class: 'WsCleanup'])
         } 
     }
     catch(clean)
     {
          emailext body: 'Attention! Fail on step \"Clean workspace before build\"', subject: 'mntlab-ci-pipeline - FAIL \"CLEAN STEP\"', to: 'bigmikola3@gmail.com'
          throw any
     }
    stage('installation') { 
        git url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'pkislouski'   
        downGradle = tool 'gradle4.6'
        downJava = tool 'java8'
    }
    
    stage('Build') {
        sh "'${downGradle}/bin/gradle' build"
    }
   
    stage ('Testing') {
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
    
    stage ('Starting child job') {
        build job: 'MNTLAB-Pavel__Kislouski-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'pkislouski']], quietPeriod: 2
    }
  
    stage ('Copy artifact from job') {
        step ([$class: 'CopyArtifact',
               projectName: 'MNTLAB-Pavel__Kislouski-child1-build-job']);
    }
    
    stage ('Unarchive & Archive') {
        sh 'cp build/libs/mntlab-ci-pipeline.jar .'
        sh 'tar -xvf child1-*.tar.gz'
         sh 'tar -czf ${WORKSPACE}/pipeline-pkislouski-${BUILD_NUMBER}.tar.gz mntlab-ci-pipeline.jar jobs.groovy'
    }
    
     try { 
         stage ('push nexus') {
              push()
         }
     }
     catch (push)
     {
          emailext body: 'Attention! Fail on step \"PUSH\"', subject: 'mntlab-ci-pipeline - FAIL \"PUSH STEP\"', to: 'bigmikola3@gmail.com'
          throw any
     }
    stage('Asking for manual approval'){
        input 'Deploy'
    }
     
     try {
         stage ('pull from nexus') {
             pull()
         }
     }
     catch (pull)
     {
          emailext body: 'Attention! Fail on step \"PULL\"', subject: 'mntlab-ci-pipeline - FAIL \"PULL STEP\"', to: 'bigmikola3@gmail.com'
          throw any
     }
     try {
         stage ('Unarchive & Execute') {
             sh 'tar -xvf nexus.tar.gz'
             sh 'java -jar mntlab-ci-pipeline.jar'
             emailext body: 'Attention! Deploy SUCCESS', subject: 'mntlab-ci-pipeline - FAIL \"SUCCESS\"', to: 'bigmikola3@gmail.com' 
             }
     }
     catch (All)
     {
          emailext body: 'Attention! Fail on step \"PULL\"', subject: 'mntlab-ci-pipeline - FAIL \"FAIL\"', to: 'bigmikola3@gmail.com'
          throw any
     }
}    
