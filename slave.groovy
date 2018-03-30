@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*
    
def version = args[1]
def authString = "YWRtaW46YWRtaW4xMjM="

if (args[0] == 'download'){
    def pull_url ="http://10.6.205.119:8081/repository/test/repository/PROD/REL/PIPELINE/${version}/PIPELINE-${version}-APP.tar.gz"
      new File("PIPELINE-${version}-APP.tar.gz").withOutputStream { out ->
          def url = new URL(pull_url.toString()).openConnection()
          url.setRequestProperty("Authorization", "Basic ${authString}")
          out << url.inputStream
      }
}
