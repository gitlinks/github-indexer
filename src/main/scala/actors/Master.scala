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
  val elasticRouter: ActorRef = context.actorOf(RoundRobinPool(10).props(Props[ElasticsearchUploader]), "router2")
  val router: ActorRef =
    context.actorOf(RoundRobinPool(1).props(Props[Worker]), "router1")
  val sdf = new SimpleDateFormat("yyyy-MM-dd")


  def warnAdmin(dateLastRun: Date) = ???

  def getEverything(): Unit = {
    try {
      log.info("Getting everything! Shit is about to get real")
      val calendar = Calendar.getInstance()
      calendar.add(Calendar.YEAR, -3)
      for (i <- 0 to 1460) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        for (i <- 0 to 23) {
          router ! Work(i, sdf.format(calendar.getTime))
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
      val currentDate = yesterdayDate

      for (i <- 0 to 23) {
        router ! Work(i, sdf.format(currentDate))
      }
      context.actorOf(Props[DerbyDAO]) ! InsertLastUpdatedDate(sdf.format(currentDate))
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
      case s =>
        if(sdf.parse(s).before(getDatePlusDays(-2))) warnAdmin(sdf.parse(s))
    }
  }

  def yesterdayDate() : Date={
    getDatePlusDays(-1)
  }

  def getDatePlusDays(days:Int): Date ={
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, days)
    calendar.getTime
  }
}
