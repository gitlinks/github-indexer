package actors

import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

import scalaj.http.Http
import akka.dispatch.{UnboundedMailbox, RequiresMessageQueue, BoundedMessageQueueSemantics}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class ElasticsearchUploader extends Actor with ActorLogging {
  val elasticEndpoint = ConfigFactory.load().getString("akka.elastic.endpoint")

  def receive: Receive = {
    case UploadToElastic(s) => {
      try {
        log.info("Uploading " + s.length + " to elasticsearch")
        s.foreach {
          case (id ,repo)=>
            if(!repo.isEmpty){
              var url = elasticEndpoint + id
              val httpRequest = Http(url).method("PUT").postData("{\"repo\": {\"name\": \"" + repo + "\"}}")
              log.debug("Response " + httpRequest.asString)
            }
        }
      } catch {
        case e: Exception => log.error(e, "Error on uploading")
      }

    }
  }
}
