


def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]
def failed_report = args[5]

if (args[0] == 'email'){
    email(status, job_name, build_number, slave_name, failed_report)
}

def email(status, job_name, build_number, slave_name, failed_report){
    def subject = status + " " + job_name + " " + build_number
    def details =  job_name + "STARTED " + "# " + build_number + "on: " + slave_name + " " + failed_report
    def details = """
        STARTED: Job ${job_name} [${build_number}]
        Runned on slave: ${slave_name}
        """
    emailext (
        to: 'skyzzz@mail.ru',
        subject: subject,
        body: details,
        attachLog: false
    )    
}
