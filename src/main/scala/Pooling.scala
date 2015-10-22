import actors.{Start, Master}
import akka.actor.{Props, ActorSystem}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
object Pooling extends App{

  val system = ActorSystem("Pooling")
  val master = system.actorOf(Props(new Master()),
    name = "master")
  master ! Start

}
