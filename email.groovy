@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*  
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import javax.mail.internet.*
import javax.mail.*
import javax.activation.*

def status = args[1]
def job_name = args[2]
def build_number = args[3]
def slave_name = args[4]
def failed_report = args[5]

if (args[0] == 'email'){
    def subject = status + " " + job_name + " " + build_number
    def details =  job_name + "STARTED " + "# " + build_number + "on: " + slave_name + " " + failed_report
    //       
    /*
    emailext (
        to: 'yomivaf@uemail99.com',
        subject: subject,
        body: details,
        attachLog: true
    )
    */
    stage('Send email') {
    def mailRecipients = "yomivaf@uemail99.com"
    def jobName = currentBuild.fullDisplayName

    emailext body: '''${SCRIPT, template="groovy-html.template"}''',
        mimeType: 'text/html',
        subject: "[Jenkins] ${jobName}",
        to: "${mailRecipients}",
        replyTo: "${mailRecipients}",
        recipientProviders: [[$class: 'CulpritsRecipientProvider']]
}
}
