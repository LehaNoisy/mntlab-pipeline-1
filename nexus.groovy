//That's my actually script.
import hudson.model.*
import  jenkins.model.Jenkins.*;

//Define a names
def act = "push"
def password = "admin:admin123"
def repository = "Realise"
def baseURL = "10.0.0.12:8081"
def GROUPID = "TomCat_group"
def ARTIFACTID = "TomCat_artifact"
def VER = "32"

//Create a parameter for the job
if("act"=="push"){
    def File = new File("/tmp/${ARTIFACTID}-${VER}.tar.gz").getBytes()
    def CONNECTION = new URL(
                             "http://${baseURL}/repository/${repository}/${GROUPID}/${ARTIFACTID}/${VER}/${ARTIFACTID}-${VER}.tar.gz"
                             ).openConnection()
  CONNECTION.setRequestProperty("Authorization" , "Basic ${password.getBytes().encodeBase64().toString()}")
    CONNECTION.setRequestMethod("PUT")
    CONNECTION.doOutput = true
    CONNECTION.setRequestProperty( "Content-Type", "application/x-gzip" )
    def writer = new DataOutputStream(CONNECTION.outputStream)
    writer.write(File)
    writer.close()
    println CONNECTION.responseCode
}

else {
    new File("/tmp/${ARTIFACTID}-${VER}.tar.gz").withOutputStream { out ->
        def url = new URL("http://${baseURL}/repository/${repository}/${GROUPID}/${ARTIFACTID}/${VER}/${ARTIFACTID}-${VER}.tar.gz").openConnection()
        url.setRequestProperty("Authorization", "Basic ${password.getBytes().encodeBase64().toString()}");
        out << url.inputStream
    }
}
