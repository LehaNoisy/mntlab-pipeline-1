def cred = "amVua2luczpqZW5raW5z"
def url = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/${PROJECT_NAME}-${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
url.setRequestProperty("Authorization" , "Basic ${cred}")
def file = new File("${WORKSPACE}/pipeline-achernak-${BUILD_NUMBER}.tar.gz")
file << url.inputStream
println url.responseCode
