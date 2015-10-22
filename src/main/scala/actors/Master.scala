package actors

import akka.routing._

import sys.process._
import java.net.URL
import java.io.File
import akka.actor.{ActorRef, Props, Actor}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class Master extends Actor{


   def receive: Receive ={
     case Start =>
       val router: ActorRef =
         context.actorOf(RoundRobinPool(23).props(Props[Worker]), "workerRouter")
       for( i <- 0 to 23){
         router ! Work(i)
       }
   }
}
