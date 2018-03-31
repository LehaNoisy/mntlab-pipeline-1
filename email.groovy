@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*

def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]
def failed_report = args[5]

if (args[0] == 'email'){
    def subject = status + " " + job_name + " " + build_number
    def details =  job_name + "STARTED " + "# " + build_number + "on: " + slave_name + " " + failed_report
    //       
    emailext (
        to: 'yomivaf@uemail99.com',
        subject: subject,
        body: details,
        attachLog: true
    )    
}
