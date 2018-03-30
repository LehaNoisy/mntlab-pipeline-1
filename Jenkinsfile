/*def push(){sh """groovy
        def cred = "amVua2luczpqZW5raW5z"
        def File = new File("${WORKSPACE}/pipeline-achernak-${BUILD_NUMBER}.tar.gz")//.getBytes()
        def connection = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/pipeline-achernak/${BUILD_NUMBER}/pipeline-achernak-${BUILD_NUMBER}.tar.gz").openConnection()
        connection.setRequestMethod("PUT")
        connection.doOutput = true
        connection.setRequestProperty("Authorization" , "Basic ${cred}")
        def writer = new DataOutputStream(connection.outputStream)
        writer.write (File)
        writer.close()
       println connection.responseCode"""}

def pull(){sh """groovy
        def cred = "amVua2luczpqZW5raW5z"
        def url = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/${PROJECT_NAME}-${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
        url.setRequestProperty("Authorization" , "Basic ${cred}")
        def file = new File("${WORKSPACE}/pipeline-achernak-${BUILD_NUMBER}.tar.gz")
        file << url.inputStream
        println url.responseCode"""}
*/
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
@NonCPS
def pull(){
    def cred = "amVua2luczpqZW5raW5z"
    def PROJECT_NAME='pipeline'
    def ARTIFACT_SUFFIX='achernak'
    def BUILD_NUMBER='96'
    def ARTIFACT_NAME=[PROJECT_NAME,ARTIFACT_SUFFIX,BUILD_NUMBER].join('-')+'.tar.gz'
    //appbackup/pipeline-achernak/96/pipeline-achernak-96.tar.gz
    def url = "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/${PROJECT_NAME}-${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}"
    def conn = url.toURL().openConnection()
    conn.setRequestProperty( "Authorization", "Basic ${authString}" )
    if( conn.responseCode == 200 )
        InputStream input = conn.getInputStream()
        byte[] buffer = new byte[4096]
        int n

        OutputStream output = new FileOutputStream( ARTIFACT_NAME );
        while ((n = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, n)
        }
        output.close()

        return ARTIFACT_NAME    
}

node("${SLAVE}") { 
    echo "Hello MNT-Lab"
    pull()
}
