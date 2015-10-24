package actors

import akka.actor.Actor

import scalaj.http.Http

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class ElasticsearchUploader extends Actor{
   def receive: Receive = {
     case UploadToElastic(s) => {
       val owner = s.split("/")(0)
       val name = s.split("/")(0)
       var url = "http://localhost:9200/github/repository/" +s.replace("/","-")
       val httpRequest = Http(url).method("PUT").postData("{repo: {\"name\": \""+s+ "\"}}")
     }
  }
}
