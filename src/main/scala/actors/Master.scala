package actors

import akka.routing.RoundRobinRouter

import sys.process._
import java.net.URL
import java.io.File
import akka.actor.{Props, Actor}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class Master extends Actor{


   def receive: Receive ={
     case Start =>
       val workerRouter = context.actorOf(
         Props[Worker].withRouter(RoundRobinRouter(23)), name = "workerRouter")
       for( i <- 0 to 23){
         workerRouter ! Work(i)
       }
   }
}
