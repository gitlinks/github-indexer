package actors

import java.util.zip.GZIPInputStream

import akka.dispatch.{UnboundedMailbox, RequiresMessageQueue}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.Json

import scala.io.Source
import sys.process._
import java.io.{BufferedInputStream, FileInputStream, File}
import java.net.{HttpURLConnection, URL}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import akka.actor.{ActorLogging, Actor}

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class Worker extends Actor with ActorLogging {

  var githubArchiveEndpoint = ConfigFactory.load().getString("akka.githubarchive.endpoint")

  def downloadAndParse(i: Int, date: String): Seq[(String,String)] = {
    log.warning("Starting download of " + githubArchiveEndpoint + date + "-" + i + ".json.gz")
    val urlIs = getUrlInputStream(githubArchiveEndpoint + date + "-" + i + ".json.gz")
    val gis = new GZIPInputStream(urlIs)
    try {
      var seqToReturn = scala.collection.mutable.MutableList[(String, String)]()
      val source = Source.fromInputStream(gis)("UTF-8")
      source.getLines().foreach(x => seqToReturn ++= List(parseSingleLine(x)))
      log.info("Download finished")
      source.close()
      seqToReturn
    } catch {
      case e: Exception => log.error(e, "Error on download and parse for "+ date +" "+i )
        Seq()
    } finally {
      gis.close()
      urlIs.close()
    }
  }

  def getUrlInputStream(url: String,
                        connectTimeout: Int = 5000,
                        readTimeout: Int = 5000,
                        requestMethod: String = "GET") = {
    val u = new URL(url)
    val conn = u.openConnection.asInstanceOf[HttpURLConnection]
    HttpURLConnection.setFollowRedirects(false)
    conn.setConnectTimeout(connectTimeout)
    conn.setReadTimeout(readTimeout)
    conn.setRequestMethod(requestMethod)
    conn.connect
    conn.getInputStream
  }

  def parseSingleLine(line: String): (String, String) = {
    val jsObject = Json.parse(line)
    (jsObject \\ "type")(0).as[String] match {
      case "CreateEvent" => {
        if ((jsObject \\ "ref_type")(0).as[String].equals("repository")) {
          (((jsObject \ "repo") \ "id").as[Int].toString ,((jsObject \ "repo") \ "name").as[String])
        }
        else ("","")
      }
      case "PushEvent" =>(((jsObject \ "repo") \ "id").as[Int].toString , ((jsObject \ "repo") \ "name").as[String])
      case _ => ("","")
    }
  }

  def receive: Receive = {
    case Work(i, date) => sender ! ResultString(downloadAndParse(i, date))
  }
}
