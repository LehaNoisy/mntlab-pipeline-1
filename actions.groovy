def actions = args[0]
def Artifact_full_name = args[1]
def Credles = "Yarm:Yarm"
def Repository = "Pipeline"
def nexus = "http://EPBYMINW2473.minsk.epam.com:8081"
def Parse = (Artifact_full_name.split("(?<=\\w)(?=[\\-\\.])|(?<=[\\-\\.])")).toList()
Parse.removeAll('-')
Parse.removeAll('.')
def Group_Id = Parse[0]
def Artifact_name = Parse[1]
def Vers = Parse[2]

if("$actions"=="push") {
    def Artifact = new File("${Artifact_full_name}").getBytes()
    def connection = new URL("${nexus}/repository/${Repository}/${Group_Id}/${Artifact_name}/${Vers}/${Artifact_full_name}").openConnection()
    println(connection)
    connection.setRequestMethod("PUT")
    connection.doOutput = true
    connection.setRequestProperty("Authorization", "Basic ${Credles.getBytes().encodeBase64().toString()}")
    def writer = new DataOutputStream(connection.outputStream)
    writer.write(Artifact)
    writer.close()
    println connection.responseCode
}
if("$actions"=="pull"){
    def Artifact = new File("${Artifact_full_name}")
    def connection = new URL("${nexus}/repository/${Repository}/${Group_Id}/${Artifact_name}/${Vers}/${Artifact_full_name}").openConnection()
    connection.setRequestProperty("Authorization" ,"Basic ${Credles.getBytes().encodeBase64().toString()}")
    Artifact << connection.inputStream
    println connection.responseCode
}
