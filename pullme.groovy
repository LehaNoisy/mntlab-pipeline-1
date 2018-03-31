def GROUP_NAME = 'mbean-groovy'
def NEXUS_URL = 'http://EPBYMINW7423.minsk.epam.com:8081'
def REPO_NAME = 'AKO-maven2-hosted-repo'
def GROUP = 'PipelineGroup'
def ARTEFACT = 'AKOart-pipeline'
def TYPE = 'tar.gz'
def artfile = "http://EPBYMINW7423.minsk.epam.com:8081/repository/AKO-maven2-hosted-repo/PipelineGroup/AKOart-pipeline/33/AKOart-pipeline-33.tar.gz"

new File("${artfile}").withOutputStream { out ->
    def AuthEncoded = "YWtvOmFrbw=="
    def version = artfile.substring(artfile.lastIndexOf("-")+1, artfile.indexOf("."))
    def opened = new URL("${NEXUS_URL}/repository/${REPO_NAME}/${GROUP}/${ARTEFACT}/${version}/${ARTEFACT}-${version}.${TYPE}").openConnection()
    opened.setRequestProperty("Authorization", authorization);
    out << opened2.inputStream
}
println artfile.responseCode
