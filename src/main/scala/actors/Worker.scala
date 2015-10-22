package actors

import sys.process._
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import akka.actor.Actor

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
class Worker extends Actor {

  def downloadAndParse(i: Int): Seq[String] = {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    val currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime)
    new URL("http://data.githubarchive.org/"+currentDate+ "-"+i+".json.gz") #> new File("/tmp/output"+i+".gzip") !!;
    return List()
  }

  def receive: Receive = {
    case Work(i) => sender ! Result(downloadAndParse(i))
  }
}
