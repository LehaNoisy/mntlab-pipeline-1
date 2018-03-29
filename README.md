Requirements for the Pipeline:
- Please see APPENDIX A.
![picture](https://image.ibb.co/m4A2Un/Screenshot_from_2018_03_27_16_08_55.png)
- Details of stages:

1) For the remote Jenkins execution should be restricted on student node (SLAVE var).
Locally it can be done where student wants
2) ‘Preparation (Checking out)’
Checkout code from {student} repo
3) ‘Building code’
 Should contain building code stage
Additional commands:
gradle build - instruction for gradle to execute build task
 
4)  ‘Testing code’
Should contain 3 parallel execution of tests
     Stages:  ‘Unit Tests’, ‘Jacoco Tests’, ‘Cucumber Tests’
Additional commands:
 
gradle cucumber – performs Cucumber tests
gradle jacocoTestReport - Generates code coverage report for the test task.
gradle test - Runs the unit tests.

5)  ‘Triggering job and fetching artefact after finishing’
On this stage the job MNTLAB-{student}-child1-job should be triggered with {student} branch parameter. (the job from previous DSL lab). Job should fetch code from ‘MNT-Lab/mntlab-dsl’ from {student} branch.
- Pipeline should wait for finishing triggered job, take created artefact.

6) ‘Packaging and Publishing results’
On this stage the job should:
a) take:
 -  ‘jobs.groovy’ file (from child1-job archive), 
- ‘Jenkinsfile’, 
- built ‘gradle-simple.jar’ 

b) Should create new artefact ‘pipeline-{student}-{buildNumber}.tar.gz’ (where buildNumber - number of the current build)
   c) Should attach this artefact to current job and Push to Nexus

7) ‘Asking for manual approval’
Once previous stage successful the job should ask for deployment this artefact.

8) 'Deployment'
Artefact should be pulled from Nexus and deployed (unpack and execute) ;
Use command ‘java -jar gradle-simple.jar’

9) !!! Implement handling  errors on each stage. The message (feedback) should be sent by email to DL (distribution list) with proper error description: failed stage, what has happened , when … etc. If pipeline status is ‘Success’ – send final email about this as well

10) !!!! Pull/Push functionality should be implemented as functions in pipelines and called in appropriate stages.
