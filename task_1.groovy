/* Example: task_1.groovy pull my_app-Jenkins-56.tar.gz */

action = /* "push" */    args[0]
def name = /*"Jenkins-52.tar.gz"*/ /* "my_app-hello-26.tar.gz"*/       args[1]
/*http://nexus/repository/storage/my-app/Jenkins/52/Jenkins-56.tar.gz*/
def nexus_name = "epbyminw2468.minsk.epam.com:8081"
def repo = "repo_for_script"

/* def path = "~/" + name */

def list_out = name.split("-")
def groupId = list_out[0]
def artifactId = list_out[1]
def ending = list_out[2]
def build_number = ending[0..ending.indexOf(".")-1]
def extension = ending[ending.indexOf(".")+1..-1]

def url = new URL("http://" + nexus_name + "/repository/" + repo + "/" + groupId + "/" + artifactId + "/" + build_number + "/" + name)
/* def test_url = new URL("http://nexus/repository/storage/my_app/Jenkins/56/Jenkins-56.tar.gz")
def test_url1 = new URL("http://nexus/repository/storage/my_app/Jenkins/95/Jenkins-95.tar.gz") */

def credentials = "nexus:nexus"
def encoded = credentials.bytes.encodeBase64()
def converted = credentials.getBytes().encodeBase64().toString()


if (action == "pull"){
    URLConnection connection = url.openConnection()
    connection.setRequestProperty("Authorization", "Basic ${encoded}")
    InputStream input = connection.getInputStream()

    byte[] buffer = new byte[4096]
    int n

    OutputStream output = new FileOutputStream(name)
    while ((n = input.read(buffer)) != -1) {
        output.write(buffer, 0, n)
    }
    output.close()

}


else if (action == "push"){
    def name_bytes = new File ("${name}").getBytes()
    URLConnection connect = url.openConnection()
    connect.setRequestProperty("Authorization" , "Basic ${converted}")
    connect.setRequestMethod("PUT")
    connect.doOutput = true
    connect.setRequestProperty("Content-Type", "application/x-gzip")
    def writer = new DataOutputStream(connect.outputStream)
    writer.write(name_bytes)
    writer.close()
    println connect.responseCode
}

else throw new Exception("Wrong action, use push or pull")

