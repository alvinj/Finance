package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.data.format.Formats._  // needed for `of[Double]` in mapping

object Transactions extends Controller {
    
  val stockForm: Form[Transaction] = Form(
    // the names you use in this mapping (such as 'symbol') must match the names that will be
    // POSTed to your methods in JSON.
    mapping(
      // verifying here creates a field-level error; if your test returns false, the error is shown
      "symbol" -> nonEmptyText,
      "ttype" -> nonEmptyText,
      "price" -> bigDecimal,
      "quantity" -> number,
      "datetime" -> date,  // date("yyyy-MM-dd")
      "notes" -> text
      )
      // stockForm -> Transaction
      ((symbol, ttype, price, quantity, datetime, notes) => Transaction(0, symbol, ttype, price, quantity, datetime, notes))
      // Transaction -> StockForm
      ((t: Transaction) => Some(t.symbol, t.ttype, t.price, t.quantity, t.datetime, t.notes))
)
  
  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  def list = Action.async {
    val transactionsAsFuture = scala.concurrent.Future{ Transaction.getAll }
    transactionsAsFuture.map(transactions => Ok(Json.toJson(transactions)))
  }

}








