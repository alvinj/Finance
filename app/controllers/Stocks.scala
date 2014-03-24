package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json.Json
import play.api.libs.json._

object Stocks extends Controller {
    
  val stockForm: Form[Stock] = Form(
    // defines a mapping that will handle Stock values.
    // the names you use in this mapping (such as 'companyName') must match the names that will be
    // POSTed to your methods in JSON.
    mapping(
      // verifying here creates a field-level error.
      // if your test returns false, the error is shown.
      "symbol" -> nonEmptyText.verifying("Doh - Stock already exists (1)!", Stock.findBySymbol(_) == 0),
      "companyName" -> nonEmptyText)
      ((symbol, companyName) => Stock(0, symbol, companyName))
      ((s: Stock) => Some((s.symbol, s.companyName)))
//      verifying("Doh - Stock already exists (2)!", fields => fields match {
//        // this block creates a 'form' error
//        // this only gets called if all field validations are okay
//        case Stock(id, symbol, company) =>  Stock.findBySymbol(symbol) == 0
//      })
  )

  /**
   * Need to return data like this (or change the client):
   * echo '{ "data": [ {"id": 1, "symbol": "AAPL", "companyName": "Apple"}, {"id": 2, "symbol": "GOOG", "companyName": "Google"}] }'
   */
  def list = Action {
    val stocks = Stock.getAll2
    Ok(Json.toJson(stocks))
  }

  /**
   * The Sencha client will send me id, symbol, and companyName in a POST request.
   * I need to return something like this on success:
   *     { "success" : true, "msg" : "", "id" : 100 }
   */
  def add = Action { implicit request =>
    println(s"content-type: ${request.contentType}")
    println(s"headers: ${request.headers}")
    println(s"body: ${request.body}")
    println(s"query string: ${request.rawQueryString}")
    import play.api.libs.json.Json._
    stockForm.bindFromRequest.fold(
      errors => {
        println("*** CAME TO STOCK > Fold > Errors ***")
        val result = Map("success" -> toJson(false), "msg" -> toJson("Boom!"), "id" -> toJson(0))
        Ok(Json.toJson(result))
      },
      stock => {
        println("*** CAME TO STOCK > Fold > Stock/Success ***")
        val id = Stock.insert(stock)
        id match {
          case Some(autoIncrementId) =>
              val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))
              Ok(Json.toJson(result))
          case None =>
              // TODO inserts can fail; i need to handle this properly.
              val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(-1))
              Ok(Json.toJson(result))
        }
        
      }
    )
  }
  
  
}





