package models

import play.api.db._
import play.api.Play.current
import anorm.SQL
import anorm.SqlQuery
import java.text.SimpleDateFormat

case class ResearchLink (
  var id: Long,
  var symbol: String,
  var url: String,
  var datetime: java.util.Date,
  var notes: Option[String]
)

object ResearchLink {

  val sqlQuery = SQL("SELECT * FROM research_links ORDER BY date_time DESC")

  def selectAll(): List[ResearchLink] = DB.withConnection { implicit connection => 
    sqlQuery().map ( row =>
      ResearchLink(
        row[Long]("id"),
        row[String]("symbol"),
        row[String]("url"),
        row[java.util.Date]("date_time"),
        row[Option[String]]("notes")
      )
    ).toList
  }

  /**
   * JSON Serializer Code
   * --------------------
   */
  import play.api.libs.json.Json
  import play.api.libs.json._

  implicit object ResearchLinkFormat extends Format[ResearchLink] {

      // convert from ResearchLink object to JSON (serializing to JSON)
      def writes(researchLink: ResearchLink): JsValue = {
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val researchLinkSeq = Seq(
              "id" -> JsNumber(researchLink.id),
              "symbol" -> JsString(researchLink.symbol),
              "url" -> JsString(researchLink.url),
              "datetime" -> JsString(sdf.format(researchLink.datetime)),
              "notes" -> JsString(researchLink.notes.getOrElse(""))
          )
          JsObject(researchLinkSeq)
      }

      // convert from a JSON string to a Transaction object (de-serializing from JSON)
      // @see http://www.playframework.com/documentation/2.2.x/ScalaJson regarding Option
      def reads(json: JsValue): JsResult[ResearchLink] = {
          val id = (json \ "id").as[Long]
          val symbol = (json \ "symbol").as[String]
          val url = (json \ "url").as[String]
          val datetime = (json \ "datetime").as[java.util.Date]
          val notes = (json \ "notes").asOpt[String]
          JsSuccess(ResearchLink(id, symbol, url, datetime, notes))
      }
  }



}








