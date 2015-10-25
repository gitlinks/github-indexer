package actors

import java.util.zip.GZIPInputStream

import akka.dispatch.{UnboundedMailbox, RequiresMessageQueue}
import play.api.libs.json.Json

import scala.io.Source
import sys.process._
import java.io.{BufferedInputStream, FileInputStream, File}
import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import akka.actor.{ActorLogging, Actor}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class Worker extends Actor  with ActorLogging {

  def downloadAndParse(i: Int, date: String): Seq[Option[String]] = {
    try {
      log.info("Starting download of " + "http://data.githubarchive.org/" + date + "-" + i + ".json.gz")
      val gis = new GZIPInputStream(new BufferedInputStream(new URL("http://data.githubarchive.org/" + date + "-" + i + ".json.gz").openStream()))
      log.info("Download finished")
      Source.fromInputStream(gis).getLines().map(parseSingleLine).toSeq
    } catch {
      case e: Exception => log.error(e, "Error on download and parse")
        Seq()
    }
  }

  def parseSingleLine(line: String): Option[String] = {
    val jsObject = Json.parse(line)
    (jsObject \\ "type")(0).as[String] match {
      case "CreateEvent" => {
        if ((jsObject \\ "ref_type")(0).as[String].equals("repository")) ((jsObject \ "repo") \ "name").asOpt[String]
        else None
      }
      case _ => None
    }
  }

  def receive: Receive = {
    case Work(i, date) => sender ! ResultString(downloadAndParse(i, date))
  }
}
