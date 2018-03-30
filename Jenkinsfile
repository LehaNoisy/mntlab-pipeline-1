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
import hudson.FilePath;
import jenkins.model.Jenkins;
def check(){
  build.workspace.isRemote()
}
node("${SLAVE}") { 
    echo "Hello MNT-Lab"
    writeFile file: 'a.txt', text: 'Hello World!';
    listFiles(createFilePath(pwd()));
    println "is Remote: "+ check()
}

def createFilePath(path) {
    if (env['NODE_NAME'] == null) {
        error "envvar NODE_NAME is not set, probably not inside an node {} or running an older version of Jenkins!";
    } else if (env['NODE_NAME'].equals("master")) {
        return new FilePath(path);
    } else {
        return new FilePath(Jenkins.getInstance().getComputer(env['NODE_NAME']).getChannel(), path);
    }
}
@NonCPS
def listFiles(rootPath) {
    print "Files in ${rootPath}:";
    for (subPath in rootPath.list()) {
        echo "  ${subPath.getName()}";
    }
}
/*
@NonCPS
def pull(){
        def authString = "amVua2luczpqZW5raW5z"
        def PROJECT_NAME='pipeline'
        def ARTIFACT_SUFFIX='achernak'
        def BUILD_NUMBER='96'
        def ARTIFACT_NAME=[PROJECT_NAME,ARTIFACT_SUFFIX,BUILD_NUMBER].join('-')+'.tar.gz'
        //appbackup/pipeline-achernak/96/pipeline-achernak-96.tar.gz
        def url = "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/${PROJECT_NAME}-${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}"
        URLConnection conn = url.toURL().openConnection()
        conn.setRequestProperty( "Authorization", "Basic ${authString}" )
        println conn.responseCode
       // println conn.inputStream.getText()
       // println conn.getInputStream()
        InputStream input = conn.getInputStream()
        byte[] buffer = new byte[4096]
        int n

        OutputStream output = new FileOutputStream( ARTIFACT_NAME );
        while ((n = input.read(buffer)) != -1)
                {
                    output.write(buffer, 0, n)
                }
        output.close()

        sh 'pwd; ls -la'
        
        channel = build.workspace.channel 
        rootDirRemote = new FilePath(channel, pwd()) 
        println "rootDirRemote::$rootDirRemote"        
   return ARTIFACT_NAME 
}

node("${SLAVE}") { 
    echo "Hello MNT-Lab"
    pull()
}
*/
