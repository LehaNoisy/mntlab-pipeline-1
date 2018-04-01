def BUILD_NUMBER = args[0]
def AuthEncoded = "YWtvOmFrbw=="
def NEXUS_URL = 'http://EPBYMINW7423.minsk.epam.com:8081'
def REPO_NAME = 'AKO-maven2-hosted-repo'
def GROUP = 'PipelineGroup'
def TYPE = 'tar.gz'
def ARTIFACT = 'AKOart-pipeline'
//def ARTFILE = "${ARTIFACT}-${BUILD_NUMBER}.${TYPE}"
def art = new File("pipeline-hkavaliova-${BUILD_NUMBER}.tar.gz").getBytes()
//def take = new URL("http://EPBYMINW7423.minsk.epam.com:8081/repository/AKO-maven2-hosted-repo/PipeGroup/AKOpipe/${BUILD_NUMBER}/pipeline-hkavaliova-${BUILD_NUMBER}.tar.gz").openConnection()
def take = new URL("${NEXUS_URL}/repository/${REPO_NAME}/${GROUP}/${ARTIFACT}/${BUILD_NUMBER}/pipeline-hkavaliova-${BUILD_NUMBER}.${TYPE}").openConnection()
take.setRequestMethod("PUT")
take.doOutput = true
take.setRequestProperty("Authorization" , "Basic ${AuthEncoded}")
def upload = new DataOutputStream(take.outputStream)
upload.write (File)
upload.close()
println take.responseCode
