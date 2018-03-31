def NUMBER = 33
def AuthEncoded = "YWtvOmFrbw=="
def NEXUS_URL = 'http://EPBYMINW7423.minsk.epam.com:8081'
def REPO_NAME = 'AKO-maven2-hosted-repo'
def GROUP = 'PipelineGroup'
def TYPE = 'tar.gz'
def ARTIFACT = 'AKOart-pipeline'
def ARTFILE = "AKOart-pipeline-${NUMBER}.${TYPE}"
def opened = new URL("${NEXUS_URL}/repository/${REPO_NAME}/pipeline/${GROUP}/${ARTIFACT}/${NUMBER}/${ARTFILE}").openConnection()
opened.setRequestProperty("Authorization", "Basic ${AuthEncoded}")
def out = new File("${ARTFILE}")
out << opened.inputStream
println opened.responseCode
