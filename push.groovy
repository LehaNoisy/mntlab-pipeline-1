def BUILD_NUMBER = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def File = new File("pipeline-ashumilau-${BUILD_NUMBER}.tar.gz").getBytes()
def connection = new URL( "http://EPBYMINW2470.minsk.epam.com:8081/repository/JenkNexus/group_pipeline/archive/${BUILD_NUMBER}/pipeline-ashumilau-${BUILD_NUMBER}.tar.gz").openConnection()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${cred}")
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.close()
println connection.responseCode
