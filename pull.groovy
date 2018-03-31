def student ="azaitsau"
def BUILD_NUMBER = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def url = new URL( "http://EPBYMINW7425.minsk.epam.com:8081/repository/ForPipekine/MNT-pipeline/Pip-artifact/${BUILD_NUMBER}/pipeline-${student}-${BUILD_NUMBER}.tar.gz").openConnection()
url.setRequestProperty("Authorization" , "Basic ${cred}")
def file = new File("pipeline-${student}-${BUILD_NUMBER}.tar.gz")
file << url.inputStream
println url.responseCode
