def BUILD_NUMBER =  args[0]
def WORKSPACE = args[1]

def authString = "pnexus:pnexus"
def CONVERTED_CREDS = "${authString}".getBytes().encodeBase64().toString()
        def url ="http://EPBYMINW7296.minsk.epam.com:8081/repository/task11/pipeline/grarc/" + BUILD_NUMBER + "/pipeline-pkislouski-" + BUILD_NUMBER + ".tar.gz"
        def file = new File(WORKSPACE + "/nexus.tar.gz")
        def down = new URL(url).openConnection()
        down.setRequestProperty("Authorization", "Basic " + CONVERTED_CREDS)
file << down.inputStream
