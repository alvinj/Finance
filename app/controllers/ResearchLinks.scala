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

  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  def list = Action.async {
    val researchLinksAsFuture = scala.concurrent.Future{ ResearchLink.selectAll }
    researchLinksAsFuture.map(researchLinks => Ok(Json.toJson(researchLinks)))
  }
  
}


