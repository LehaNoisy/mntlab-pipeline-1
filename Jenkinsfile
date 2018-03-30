node {
     def downGradle
     def downJava
    stage('installation') { 
      git url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'pkislouski'   
      downGradle = tool 'gradle4.6'
      downJava = tool 'java8'
    }
    stage('Build') {
      sh "'${downGradle}/bin/gradle' build"
    }
    stage('Results') {
     archive 'target/*.jar'
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
}
