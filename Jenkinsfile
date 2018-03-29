node {
     def downGradle
     def downJava
    stage('Preparation') { // for display purposes
      git 'https://github.com/MNT-Lab/mntlab-pipeline.git'
      downGradle = tool 'gradle4.6'
      downJava = tool 'java8'
    }
    stage('Build') {
      sh "'${downGradle}/bin/gradle' build"
    }
    stage('Results') {
     archive 'target/*.jar'
    }
}
