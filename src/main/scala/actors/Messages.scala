package actors

/**
 * Created by brunnoattorre1 on 10/22/15.
 */
sealed trait Messages
case class Work(i: Int) extends Messages
case object Start extends Messages
case class Result(seq: Seq[String]) extends Messages
case class UploadToElastic(seq: Seq[String]) extends Messages