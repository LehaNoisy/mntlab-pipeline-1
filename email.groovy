def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]

if (args[0] == 'email'){
    def log = currentBuild.rawBuild.getLog(20).join('\n\t\t')
    def subject = "${status}: Job '${job_name} [${build_number}]'"
    def details = """STARTED: Job    ${jobname} [${build_number}]
        Runned on slave: ${slave_name}
        
        Last 20 lines in log:
        ${log}"""
    emailext (
        to: 'yomivaf@uemail99.com',
        subject: subject,
        body: details,
        attachLog: true
    )    
}

// https://temp-mail.org/ru/
