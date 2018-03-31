def STUDENT = "ashumilau"
def BUILD_NUMBER = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def url = new URL( "http://EPBYMINW2470.minsk.epam.com:8081/repository/JenkNexus/group_pipeline/archive/${BUILD_NUMBER}/pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz").openConnection()
url.setRequestProperty("Authorization" , "Basic ${cred}")
def file = new File("pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz")
file << url.inputStream
println url.responseCode
