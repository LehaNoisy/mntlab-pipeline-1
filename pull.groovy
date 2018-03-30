def WS = args[0]
def BN = args[1]
def cred = "amVua2luczpqZW5raW5z"
def url = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/pipeline-achernak/${BN}/pipeline-achernak-${BN}.tar.gz").openConnection()
url.setRequestProperty("Authorization" , "Basic ${cred}")
def file = new File("${WS}/pipeline-achernak-${BN}.tar.gz")
file << url.inputStream
println url.responseCode
