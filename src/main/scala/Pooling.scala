import actors.{InitScheduler, Start, Master}
import akka.actor._
import akka.event.Logging
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by brunnoattorre1 on 10/22/15.
 */
object Pooling extends App {
  val system = ActorSystem("Pooling")
  val master = system.actorOf(Props(new Master()),
    name = "master")
  master ! InitScheduler
}
