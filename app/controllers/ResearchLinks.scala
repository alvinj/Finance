package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.data.format.Formats._  // needed for `of[Double]` in mapping
import java.util.Calendar

object ResearchLinks extends Controller {

  // note: skipping `id` field
  // note: skipping `datetime` field
  val researchLinkForm: Form[ResearchLink] = Form(
    mapping(
      "symbol" -> nonEmptyText,
      "url" -> nonEmptyText,
      "notes" -> optional(text)
    )
    // researchLinkForm -> ResearchLink
    ((symbol, url, notes) => ResearchLink(0, symbol, url, Calendar.getInstance.getTime, notes))
    // ResearchLink -> researchLinkForm
    ((rl: ResearchLink) => Some(rl.symbol, rl.url, rl.notes))
  )

  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  def list = Action.async {
    val researchLinksAsFuture = scala.concurrent.Future{ ResearchLink.selectAll }
    researchLinksAsFuture.map(researchLinks => Ok(Json.toJson(researchLinks)))
  }

  /**
   * The Sencha client will send me `symbol`, `url`, and `notes` in a POST request.
   * I need to return something like this on success:
   *     { "success" : true, "msg" : "", "id" : 100 }
   * Return an HTTP 200 error status on error.
   */
  def add = Action { implicit request =>
    researchLinkForm.bindFromRequest.fold(
      errors => {
        // TODO return an HTTP 200 error here instead?
        Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Boom!"), "id" -> toJson(0))))
      },
      researchLink => {
        val id = ResearchLink.insert(researchLink)
        id match {
          case Some(autoIncrementId) =>
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))))
          case None =>
              // TODO inserts can fail; i need to handle this properly.
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(-1))))
        }
        
      }
    )
  }

  /**
   * Delete a transaction, asynchronously.
   */
  def delete(id: Long) = Action.async {
    val futureNumRowsDeleted = scala.concurrent.Future{ ResearchLink.delete(id) }
    // TODO handle the case where 'count < 1' properly 
    futureNumRowsDeleted.map{ count =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Link was deleted"), "id" -> toJson(count))
        Ok(Json.toJson(result))
    }
  }
  
  
}






