package models

import play.api.db._
import play.api.Play.current
import anorm.SQL
import anorm.SqlQuery
import java.text.SimpleDateFormat

case class ResearchLink (
  var id: Long,
  var uid: Long,
  var symbol: String,
  var url: String,
  var datetime: java.util.Date,
  var notes: Option[String]
)

object ResearchLink extends SqlModel {

  val sqlQuery = SQL("SELECT * FROM research_links ORDER BY date_time DESC")

  def selectAll(): List[ResearchLink] = DB.withConnection { implicit connection => 
    sqlQuery().map ( row =>
      ResearchLink(
        row[Long]("id"),
        row[Long]("uid"),
        row[String]("symbol"),
        row[String]("url"),
        row[java.util.Date]("date_time"),
        row[Option[String]]("notes")
      )
    ).toList
  }

  def insert(researchLink: ResearchLink): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into research_links (symbol, url, notes) values ({symbol}, {url}, {notes})")
        .on(
          'symbol -> researchLink.symbol,
          'url -> researchLink.url,
          'notes -> researchLink.notes
        ).executeInsert()
      }
    id
  }

  def delete(id: Long): Int = delete(id, "research_links")
//  def delete(id: Long): Int = {
//    DB.withConnection { implicit c =>
//      val nRowsDeleted = SQL("delete from research_links where id = {id}")
//        .on('id -> id)
//        .executeUpdate()
//      nRowsDeleted
//    }
//  }

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
              "uid" -> JsNumber(researchLink.uid),
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
          val uid = (json \ "uid").as[Long]
          val symbol = (json \ "symbol").as[String]
          val url = (json \ "url").as[String]
          val datetime = (json \ "datetime").as[java.util.Date]
          val notes = (json \ "notes").asOpt[String]
          JsSuccess(ResearchLink(id, uid, symbol, url, datetime, notes))
      }
  }



}








