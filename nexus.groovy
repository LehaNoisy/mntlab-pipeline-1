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
        def connection = new URL(NEXUS_URL.toString()).openConnection()
        connection.setRequestProperty("Authorization", "Basic ${authString}")
        out << connection.inputStream
    }
}
else if (args[0] == 'push'){
    def connection = new URL(NEXUS_URL).openConnection()
    connection.doOutput = true
    connection.setRequestMethod("PUT")
    connection.setRequestProperty("Authorization", "Basic ${authString}")
    connection.setRequestProperty("Content-Type", "application/x-gzip")
    def UploadFile = new DataOutputStream(connection.outputStream)
    UploadFile.write(new File ("${fileName}").getBytes())
    UploadFile.close()
    if(connection.responseCode == 201) {
        println "SUCCESS"
    } else{
        println "Upload failed. Response code ${connection.responseCode}"
        System.exit(1)
    }
}
