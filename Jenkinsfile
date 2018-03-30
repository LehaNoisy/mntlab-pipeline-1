def student = "ashumilau"

node(env.SLAVE){
    def downGradle
    def downJava
    stage ('Checking out') {
    git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    downGradle = tool 'gradle4.6' 
    downJava = tool 'java8'
    }
    stage('Build') {
         sh "'${downGradle}/bin/gradle' build"
      }
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
     stage("Triggering job and fetching artefact after finishing") {
        sh 'rm -rf *.tar.gz'
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: "ChooseBranch", value: "${student}")]
        step ([$class: "CopyArtifact",
               projectName: "MNTLAB-${student}-child1-build-job",
               filter: "*.tar.gz"])
    }
    stage ('Packaging and Publishing results') {
        sh 'tar xvf *.tar.gz' 	
        sh 'tar -czf pipeline-ashumilau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-ashumilau-${BUILD_NUMBER}.tar.gz'
        nexusArtifactUploader artifacts: [[artifactId: 'Pip-artifact', classifier: 'app', file: 'pipeline-ashumilau-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'MNT-pipeline', nexusUrl: '10.6.204.121:8081/', nexusVersion: 'nexus3', protocol: 'http', repository: 'Realise', version: '${BUILD_NUMBER}'
}
        
    
    stage('Asking for manual approval') {
    timeout(time: 5, unit: 'MINUTES') {
        input message: 'Do you want to release this build?', ok: 'Yes' 
       }
   }
}
