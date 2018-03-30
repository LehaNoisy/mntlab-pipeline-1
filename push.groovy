def BUILD_NUMBER = args[0]
def cred = "admin:admin123"
def File = new File("pipeline-nbuzin-${BUILD_NUMBER}.tar.gz").getBytes()
def connection = new URL( "http://EPBYMINW2629.minsk.epam.com:8081/repository/NEXUS-JENKINS/group_pipeline/archive/${BUILD_NUMBER}/pipeline-nbuzin-${BUILD_NUMBER}.tar.gz").openConnection()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${cred}")
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.close()
println connection.responseCode
