//USAGE
//pull artifacts-MyHello-58.tar.gz
//push artifacts-MyHello-58.tar.gz
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*

def authString = "YWRtaW46YWRtaW4xMjM="
def repo = "artifact-repo"
def rev = args[1]

if (args[0] == 'pull'){
    def pull_url ="http://EPBYMINW1766.minsk.epam.com:8081/repository/artifact-repo/Pipeline/EasyHello/${rev}/pipeline-ykhodzin-${rev}.tar.gz"
    new File("download-${rev}.tar.gz").withOutputStream { out ->
        def url = new URL(pull_url.toString()).openConnection()
        url.setRequestProperty("Authorization", "Basic ${authString}")
        out << url.inputStream
    }
}
if (args[0] == 'push'){
    def url = new URL("http://EPBYMINW1766.minsk.epam.com:8081/repository/artifact-repo/Pipeline/EasyHello/${rev}/pipeline-ykhodzin-${rev}.tar.gz").openConnection()
    url.doOutput = true
    url.setRequestMethod("PUT")
    url.setRequestProperty("Authorization", "Basic ${authString}")
    url.setRequestProperty("Content-Type", "application/x-gzip")
    def out = new DataOutputStream(url.outputStream)
    out.write(new File ("pipeline-ykhodzin-${rev}.tar.gz").getBytes())
    out.close()
    println url.responseCode
}
