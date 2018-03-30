@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import static groovyx.net.http.ContentType.*

if (args[0] == 'pull'){
    def pull_url ="http://10.6.205.119:8081/repository/test/repository/PROD/REL/PIPELINE/48/PIPELINE-48-APP.tar.gz"
      new File("PIPELINE-48-APP.tar.gz").withOutputStream { out ->
          def url = new URL(pull_url.toString()).openConnection()
          url.setRequestProperty("Authorization", "Basic ${authString}")
          out << url.inputStream
}
