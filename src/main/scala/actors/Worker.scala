package actors

import java.util.zip.GZIPInputStream

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
class Worker extends Actor with ActorLogging{

  def downloadAndParse(i: Int): Seq[Option[String]] = {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    val currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime)
    log.info("Starting download of "+"http://data.githubarchive.org/"+currentDate+ "-"+i+".json.gz")
    val gis = new GZIPInputStream(new BufferedInputStream(new URL("http://data.githubarchive.org/"+currentDate+ "-"+i+".json.gz").openStream()))
    log.info("Download finished")
    Source.fromInputStream(gis).getLines().map(parseSingleLine).toSeq
  }

  def parseSingleLine(line: String): Option[String] = {
    val jsObject = Json.parse(line)
    (jsObject \\ "type")(0).as[String] match {
      case "CreateEvent" =>{
        if((jsObject \\ "ref_type")(0).as[String].equals("repository") ) ((jsObject \"repo") \ "name").asOpt[String]
        else None
      }
      case _ => None
    }
  }

  def receive: Receive = {
    case Work(i) => sender ! ResultString(downloadAndParse(i))
  }
}
