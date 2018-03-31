def BUILD_NUMBER = arg[0]
def WORKSPACE = arg[1]
def authString = "cG5leHVzOnBuZXh1cw=="
        def url ="http://EPBYMINW7296.minsk.epam.com:8081/repository/task11/pipeline/grarc/"+ BUILD_NUMBER "/pipeline-pkislouski-" + BUILD_NUMBER + ".tar.gz"
        def http = new URL(url).openConnection()
        http.doOutput = true
        http.setRequestMethod("PUT")
        http.setRequestProperty("Authorization", "Basic" + authString)
        http.setRequestProperty("Content-Type", "application/x-gzip")
        def out = new DataOutputStream(http.outputStream)
        def test = new File(WORKSPACE + "/pipeline-pkislouski-" + BUILD_NUMBER + ".tar.gz")
        out.write(test.getBytes())
        out.close()
println http.responseCode
