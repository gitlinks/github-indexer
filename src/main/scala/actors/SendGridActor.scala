package actors

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Actor}
import com.sendgrid.SendGrid
import com.typesafe.config.ConfigFactory

/**
 * Created by brunnoattorre1 on 10/25/15.
 */
class SendGridActor extends Actor with ActorLogging {
  val sg = new SendGrid(ConfigFactory.load().getString("akka.sendgrid.key"))
  val sgRecipients = ConfigFactory.load().getString("sendgrid.recipients").split(",")


  def sendEmail(s: String): Unit = {
    val email = new SendGrid.Email()
    email.setTo(sgRecipients)
    email.setText("Error on date "+s)
    email.setSubject("Error detected on akka github loader")
    sg.send(email)
  }

  override def receive: Receive = {
    case SendEmail(s) => sendEmail(s)
  }
}
