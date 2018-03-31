node ("${SLAVE}") {
     def downGradle
     def downJava
     stage('Clean workspace before build') {
        step([$class: 'WsCleanup'])
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
        sh 'tar -czf pipeline-pkislouski-${BUILD_NUMBER}.tar.gz mntlab-ci-pipeline.jar jobs.groovy'
    }
    
    stage ('push nexus') {
        def authString = "cG5leHVzOnBuZXh1cw=="
        def url ="http://EPBYMINW7296.minsk.epam.com:8081/repository/task11/pipeline/grarc/${BUILD_NUMBER}/pipeline-pkislouski-${BUILD_NUMBER}.tar.gz"
        def http = new URL(url).openConnection()
        http.doOutput = true
        http.setRequestMethod("PUT")
        http.setRequestProperty("Authorization", "Basic ${authString}")
        http.setRequestProperty("Content-Type", "application/x-gzip")
        def out = new DataOutputStream(http.outputStream)
        def test = new File("${WORKSPACE}/pipeline-pkislouski-${BUILD_NUMBER}.tar.gz")
        out.write(test.getBytes())
        out.close()
        println http.responseCode
    }
    
    stage('Asking for manual approval'){
        input 'Deploy'
    }
    
    stage ('pull from nexus') {
        def authString = "cG5leHVzOnBuZXh1cw=="
        def url ="http://EPBYMINW7296.minsk.epam.com:8081/repository/task11/pipeline/grarc/90/pipeline-pkislouski-90.tar.gz"
        def file = new File("${WORKSPACE}/nexus.tar.gz")
        def down = new URL(url).openConnection()
        down.setRequestProperty("Authorization", "Basic ${authString}")
        file << down.inputStream
    }
    
    stage ('Unarchive & Execute') {
        sh 'tar -xvf nexus.tar.gz'
        sh 'java -jar mntlab-ci-pipeline.jar'
    }
}    
