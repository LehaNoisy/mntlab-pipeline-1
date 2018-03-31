def student = "azaitsau"
def BN = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def File = new File("pipeline-${student}-${BUILD_NUMBER}.tar.gz").getBytes()
def url = new URL( "http://EPBYMINW7425.minsk.epam.com:8081/repository/Realise/MNT-pipeline/Pip-artifact/${BN}/pipeline-${student}-${BN}.tar.gz").openConnection()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${cred}")
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.close()
println connection.responseCode
