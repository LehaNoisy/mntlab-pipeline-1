


def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]
def failed_report = args[5]

if (args[0] == 'email'){
    //def subject = status + " " + job_name + " " + build_number
    //def details =  job_name + "STARTED " + "# " + build_number + "on: " + slave_name + " " + failed_report
    
    email(status, job_name, build_number, slave_name, failed_report)
}

def email(status, job_name, build_number, slave_name, failed_report){
    //def log = currentBuild.rawBuild.getLog(20).join('\n\t\t')
    //def subject = "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    //def consURL = new URL("${env.BUILD_URL}consoleText")
    
    def details = """
        STARTED: Job ${job_name} [${build_number}]
        Runned on slave: ${slave_name}
        Log:
        ${log}"""
    emailext (
        to: 'yomivaf@uemail99.com',
        subject: "123",
        body: "123"
    )    
}
