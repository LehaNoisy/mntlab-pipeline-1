def student = "azaitsau"

node(env.SLAVE){
    def downGradle
    def downJava
    stage ('Checking out') {
    git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    downGradle = tool 'gradle4.6' 
    downJava = tool 'java8'
    }
    stage('Build') {
      if (isUnix()) {
         sh "'${downGradle}/bin/gradle' build"
      } else {
         bat(/"${downGradle}\bin\gradle" build/)
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
        build job: "MNTLAB-${STUDENT}-child1-build-job", parameters: [string(name: "ChooseBranch", value: "${STUDENT}")]
        step ([$class: "CopyArtifact",
               projectName: "MNTLAB-${STUDENT}-child1-build-job",
               filter: "*.tar.gz"])
    }
}
