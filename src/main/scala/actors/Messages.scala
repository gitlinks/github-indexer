package actors

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
sealed trait Messages
case class Work(i: Int, date: String) extends Messages
case object Start extends Messages
case class ResultString(seq: Seq[(String,String)]) extends Messages
case class UploadToElastic(s: Seq[(String,String)]) extends Messages
case class InsertLastUpdatedDate(s: String) extends Messages
case object GetLastUpdatedDate extends Messages
case class ReceiveLastUpdateDate(s: String) extends Messages
case object InitDatabase extends Messages
case class SendEmail(s:String) extends Messages