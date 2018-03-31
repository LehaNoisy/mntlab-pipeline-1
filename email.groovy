package org.mycompany
import org.springframework.mail.*

def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]
def failed_report = args[5]

if (args[0] == 'email'){
    def subject = status + " " + job_name + " " + build_number
    def details =  job_name + "STARTED " + "# " + build_number + "on: " + slave_name + " " + failed_report

    def MailSender mailSender
    def SimpleMailMessage templateMessage
    def sendEmail(internalBuild) {
        def msg = new SimpleMailMessage(templateMessage)
        msg.text = 'Successful build for ' + new Date()
        msg.from = 'yomivaf@uemail99.com'
        mailSender.send(msg)
    }    
}
