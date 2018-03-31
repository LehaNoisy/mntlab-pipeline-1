def BUILD_NUMBER = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def File = new File("pipeline-azaitsau-${BUILD_NUMBER}.tar.gz").getBytes()
def connection = new URL( "http://EPBYMINW7425.minsk.epam.com:8081/repository/ForPipekine/MNT-pipeline/Pip-artifact/${BUILD_NUMBER}/pipeline-azaitsau-${BUILD_NUMBER}.tar.gz").openConnection()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${cred}")
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.close()
println connection.responseCode
