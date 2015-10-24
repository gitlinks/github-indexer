package actors

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import akka.routing._

import scala.io.Source
import sys.process._
import java.net.URL
import java.io.{PrintWriter, File}
import akka.actor.{ActorLogging, ActorRef, Props, Actor}

class Master extends Actor with ActorLogging{
  val elasticRouter: ActorRef = context.actorOf(RoundRobinPool(10).props(Props[ElasticsearchUploader]), "elasticRouter")
  val sdf = new SimpleDateFormat("yyyy-MM-dd")
  val lastRunFile = new File("last-run.txt")


  def warnAdmin(dateLastRun: Date) = ???

  def receive: Receive = {
    case Start =>
      log.info("Start")
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_MONTH, -2)
      Source.fromFile(lastRunFile).getLines().foreach{
        s => if(sdf.parse(s).before(cal.getTime)) warnAdmin(sdf.parse(s))
      }
      val router: ActorRef =
        context.actorOf(RoundRobinPool(23).props(Props[Worker]), "workerRouter")
      for (i <- 0 to 23) {
        router ! Work(i)
      }
    case ResultString(list) =>
      log.info("Starting to send repos to elasticsearch")
      val writer = new PrintWriter(lastRunFile)
      writer.write(sdf.format(new Date()))
      writer.close()
      list.foreach {
        case Some(s) =>
          elasticRouter ! UploadToElastic(s)
        case _ =>
      }
  }
}
