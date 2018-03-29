node {
   def downGradle
   def downJava
   stage('Preparation') { // for display purposes
      git 'https://github.com/MNT-Lab/build-principals.git'
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
}
