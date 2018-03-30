def build = args[0]
def cred = "YWRtaW46YWRtaW4xMjM="
def filename = "pipeline-$build.tar.gz"
def pull_url = new URL("http://epbyminw2471.minsk.epam.com:8081/repository/uvalchkou/pipeline/pipeline/${BUILD_NUMBER}/${filename}").openConnection()
pull_url.setRequestProperty("Authorization", "Basic ${cred}")
def file = new File("${WORKSPACE}/${filename}")
file << pull_url.inputStream
println pull_url.responseCode
