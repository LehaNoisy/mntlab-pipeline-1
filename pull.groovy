def student ="azaitsau"
def BN = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def url = new URL( "http://EPBYMINW7425.minsk.epam.com:8081/repository/Realise/MNT-pipeline/Pip-artifact/${BN}/pipeline-${student}-${BN}.tar.gz").openConnection()
url.setRequestProperty("Authorization" , "Basic ${cred}")
def file = new File("pipeline-${student}-${BN}.tar.gz")
file << url.inputStream
println url.responseCode
