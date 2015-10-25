package DAO

import java.sql.DriverManager

import actors.{ReceiveLastUpdateDate, ResultString, GetLastUpdatedDate, InsertLastUpdatedDate}
import akka.actor.{ActorLogging, Actor}
import akka.actor.Actor.Receive

/**
 * Created by brunnoattorre1 on 10/24/15.
 */
class DerbyDAO extends Actor with ActorLogging{

  Class.forName("org.apache.derby.jdbc.EmbeddedDriver")

  val createQuery = "CREATE TABLE LOG (TIMESTAMP_STRING VARCHAR(10)) "
  val insertQuery = "INSERT INTO LOG (TIMESTAMP_STRING) VALUES (\"%s\")"
  val getQuery= "SELECT * FROM LOG ORDER BY TIMESTAMP_STRING DESC"
  val conn =  DriverManager.getConnection("jdbc:derby:derbyDB;create=true")

  override def preStart(){
    val st = conn.createStatement()
    try {
      st.execute(createQuery)
    }catch {
      case e: Exception => log.error(e, "Error on prestart")
    }finally {
      st.close()
    }
    }
  override def postStop(){
    DriverManager.getConnection("jdbc:derby:derbyDB;shutdown=true");
  }

  def insertDate(s: String): Unit = {
    val st = conn.createStatement()
    try{
      st.execute(insertQuery.replace("%s", s))
    }catch{
      case e:Exception => log.error(e, "Error on Inserting date")
    }finally {
      st.close()
    }
  }

  def getDate(): String = {
    val st = conn.createStatement()
    try{
      val rs = st.executeQuery(getQuery)
      rs.next()
      rs.getString(0)
    }catch{
      case e:Exception => log.error(e, "Error on Inserting date")
        ""
    }finally {
      st.close()
    }
  }

  override def receive: Receive = {
    case InsertLastUpdatedDate(s) => insertDate(s)
    case GetLastUpdatedDate =>
      val lastDate = getDate()
      sender() !  ReceiveLastUpdateDate(lastDate)
  }
}
