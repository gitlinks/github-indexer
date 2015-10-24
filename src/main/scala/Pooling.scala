import actors.{Start, Master}
import akka.actor.{Props, ActorSystem}
import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by brunnoattorre1 on 10/22/15.
 */
object Pooling extends App{

  val system = ActorSystem("Pooling")
  val master = system.actorOf(Props(new Master()),
    name = "master")

  system.scheduler.schedule(1 minute ,
    1 day,
    master,
    Start)

}
