package actors

import akka.actor.Actor

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class ElasticsearchUploader extends Actor{
   def receive: Receive = {
     case UploadToElastic =>
  }
}
