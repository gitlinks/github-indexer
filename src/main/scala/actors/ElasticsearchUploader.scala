package actors

import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

import scalaj.http.Http
import akka.dispatch.{UnboundedMailbox, RequiresMessageQueue, BoundedMessageQueueSemantics}

class ElasticsearchUploader extends Actor with ActorLogging {
  val elasticEndpoint = ConfigFactory.load().getString("akka.elastic.endpoint")

  def receive: Receive = {
    case UploadToElastic(s) => {
      try {
        log.info("Uploading " + s + " to elasticsearch")
        s.foreach {
          case Some(repo) =>
            val owner = repo.split("/")(0)
            val name = repo.split("/")(1)
            var url = elasticEndpoint + repo.replace("/", "-")
            val httpRequest = Http(url).method("PUT").header("Charset", "UTF-8").postData("{repo: {\"name\": \"" + repo + "\"}}")
            log.debug("Response " + httpRequest.asString)
          case None =>
        }
      } catch {
        case e: Exception => log.error(e, "Error on uploading")
      }

    }
  }
}
