package actors

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import DAO.DerbyDAO
import akka.routing._

import scala.io.Source
import sys.process._
import java.net.URL
import java.io.{PrintWriter, File}
import akka.actor.{ActorLogging, ActorRef, Props, Actor}

class Master extends Actor with ActorLogging {
  val elasticRouter: ActorRef = context.actorOf(RoundRobinPool(1).props(Props[ElasticsearchUploader]), "elasticRouter")
  val router: ActorRef =
    context.actorOf(RoundRobinPool(1).props(Props[Worker]), "workerRouter")
  val sdf = new SimpleDateFormat("yyyy-MM-dd")
  val lastRunFile = new File("last-run.txt")


  def warnAdmin(dateLastRun: Date) = ???

  def getEverything(): Unit = {
    try {
      log.info("Getting everything! Shit is about to get real")
      val calendar = Calendar.getInstance()
      calendar.add(Calendar.YEAR, -4)
      val date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime)

      for (i <- 0 to 1460) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        for (i <- 0 to 23) {
          router ! Work(i, new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime))
        }
      }
    } catch {
      case e: Exception => log.error(e, "Error to send Everything")
    }
  }

  def receive: Receive = {
    case Start =>
      log.info("Start")
      context.actorOf(Props[DerbyDAO]) ! GetLastUpdatedDate
      val calendar = Calendar.getInstance()
      calendar.add(Calendar.DATE, -1)
      val currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime)

      for (i <- 0 to 23) {
         router ! Work(i, currentDate)
      }
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_MONTH, -2)
      context.actorOf(Props[DerbyDAO]) ! InsertLastUpdatedDate(sdf.format(cal.getTime))
    case ResultString(list) =>
      try {
        log.info("Starting to send repos to elasticsearch")
        elasticRouter ! UploadToElastic(list)

      } catch {
        case e: Exception => log.error(e, "Error to send to elastic")
      }
    case ReceiveLastUpdateDate(s) => s match {
      case "" =>
        context.actorOf(Props[DerbyDAO]) ! InitDatabase
        getEverything()

    }
  }
}
