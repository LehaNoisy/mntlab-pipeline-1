@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*
def authString = "YWRtaW46YWRtaW4xMjM="
def repository = "Maven_Artefacts"
def groupId = "Artefacts"
def artefName = "pipeline"
def version = args[1]
def fileName = args[2]
def NEXUS_URL = "http://EPBYMINW2033.minsk.epam.com:8081/repository/${repository}/${groupId}/${artefName}/${version}/${fileName}"
println NEXUS_URL

if (args[0] == 'pull'){
    new File("pulled-${version}.tar.gz").withOutputStream { out ->
        def url = new URL(NEXUS_URL.toString()).openConnection()
        url.setRequestProperty("Authorization", "Basic ${authString}")
        out << url.inputStream
    }
}
else if (args[0] == 'push'){
    def url = new URL(NEXUS_URL).openConnection()
    url.doOutput = true
    url.setRequestMethod("PUT")
    url.setRequestProperty("Authorization", "Basic ${authString}")
    url.setRequestProperty("Content-Type", "application/x-gzip")
    def out = new DataOutputStream(url.outputStream)
    out.write(new File ("${fileName}").getBytes())
    out.close()
    println url.responseCode
}
