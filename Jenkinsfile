def student = "ashumilau"

node("${SLAVE}"){ 
    def downGradle
    def downJava
    stage ('Checking out') {
    git branch: "${student}", url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    downGradle = tool 'gradle4.6' 
    downJava = tool 'java8'
    }
    stage('Build') {
      if (isUnix()) {
         sh "${downGradle}/bin/gradle build"
      } else {
         bat(/"${downGradle}\bin\gradle" build/)
      }
   }
   stage('Results') {
      archive 'target/*.jar'
}
}
