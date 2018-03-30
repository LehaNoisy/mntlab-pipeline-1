def BN = args[0]
def cred = "amVua2luczpqZW5raW5z"
def connection = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/pipeline-achernak/${BN}/pipeline-achernak-${BN}.tar.gz").openConnection()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${cred}")
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.close()
println connection.responseCode
