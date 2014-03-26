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
import play.api.libs.json.Json._

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
  
  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  /**
   * Need to return data like this (or change the client):
   * echo '{ "data": [ {"id": 1, "symbol": "AAPL", "companyName": "Apple"}, {"id": 2, "symbol": "GOOG", "companyName": "Google"}] }'
   * 
   * Now doing this with "async.
   * @see http://www.playframework.com/documentation/2.2.x/ScalaAsync
   * 
   * "A Future[Result] will eventually be redeemed with a value of type Result. 
   * By giving a Future[Result] instead of a normal Result, we can quickly generate 
   * the result without blocking. Then, Play will serve this result as soon as the promise is redeemed.
   * The web client will be blocked while waiting for the response, but nothing will be blocked on the server, 
   * and server resources can be used to serve other clients."
   */
  def list = Action.async {
    val futureStocks = scala.concurrent.Future{ Stock.getAll }
    futureStocks.map(stocks => Ok(Json.toJson(stocks)))
  }

//  /**
//   * Need to return data like this (or change the client):
//   * echo '{ "data": [ {"id": 1, "symbol": "AAPL", "companyName": "Apple"}, {"id": 2, "symbol": "GOOG", "companyName": "Google"}] }'
//   */
//  def list = Action {
//    val stocks = Stock.getAll2
//    Ok(Json.toJson(stocks))
//  }
//
  /**
   * The Sencha client will send me id, symbol, and companyName in a POST request.
   * I need to return something like this on success:
   *     { "success" : true, "msg" : "", "id" : 100 }
   */
  def add = Action { implicit request =>
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

  /**
   * A new "async" delete action.
   */
  def delete(id: Long) = Action.async {
    val futureNumRowsDeleted = scala.concurrent.Future{ Stock.delete(id) }
    // TODO handle the case where 'count < 1' properly 
    futureNumRowsDeleted.map{ count =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Stock was deleted"), "id" -> toJson(count))
        Ok(Json.toJson(result))
    }
  }
  
  // original, non-async method
//  def delete(id: Long) = Action {
//    val numRowsDeleted = Stock.delete(id)
//    val result = Map("success" -> toJson(true), "msg" -> toJson("Stock was deleted"), "id" -> toJson(numRowsDeleted))
//    Ok(Json.toJson(result))
//  }
  
  
}








