@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*

def authString = "YWRtaW46YWRtaW4xMjM="
def repo = "Realise"
def rev = args[1]


if (args[0] == 'pull'){
    def pull_url ="http://EPBYMINW7425.minsk.epam.com:8081/repository/Realise//MNT-pipeline/Pip-artifact/${BUILD_NUMBER}/pipeline-azaitsau-${BUILD_NUMBER}.tar.gz"
    new File("download-${BUILD_NUMBER}.tar.gz").withOutputStream { out ->
        def url = new URL(pull_url.toString()).openConnection()
        url.setRequestProperty("Authorization", "Basic ${authString}")
        out << url.inputStream
    }
}
else {
    def url = new URL("http://EPBYMINW7425.minsk.epam.com:8081/repository/Realise/Pipeline/EasyHello/${BUILD_NUMBER}/pipeline-azaitsau-${BUILD_NUMBER}.tar.gz").openConnection()
    url.doOutput = true
    url.setRequestMethod("PUT")
    url.setRequestProperty("Authorization", "Basic ${authString}")
    url.setRequestProperty("Content-Type", "application/x-gzip")
    def out = new DataOutputStream(url.outputStream)
    out.write(new File ("pipeline-azaitsau-${BUILD_NUMBER}.tar.gz").getBytes())
    out.close()
    println url.responseCode
}
