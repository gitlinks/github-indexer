package actors

import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

import scalaj.http.Http

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class ElasticsearchUploader extends Actor with ActorLogging{
  val elasticEndpoint = ConfigFactory.load().getString("akka.elastic.endpoint")

  def receive: Receive = {
     case UploadToElastic(s) => {
       log.debug("Uploading "+ s+ " to elasticsearch")
       val owner = s.split("/")(0)
       val name = s.split("/")(0)
       var url = elasticEndpoint +s.replace("/","-")
      // val httpRequest = Http(url).method("PUT").postData("{repo: {\"name\": \""+s+ "\"}}")
       //log.debug("Response "+ httpRequest.asString)

     }
  }
}
