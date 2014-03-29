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
import java.util.Calendar

object Transactions extends Controller {
    
  val transactionForm: Form[Transaction] = Form(
    // the names you use in this mapping (such as 'symbol') must match the names that will be
    // POSTed to your methods in JSON.
    // note: skipping `id` field
    // note: skipping `datetime` field
    mapping(
      // verifying here creates a field-level error; if your test returns false, the error is shown
      "symbol" -> nonEmptyText,
      "ttype" -> nonEmptyText,
      "price" -> bigDecimal,
      "quantity" -> number,
      "notes" -> text
      )
      // transactionForm -> Transaction
      ((symbol, ttype, price, quantity, notes) => Transaction(0, symbol, ttype, price, quantity, Calendar.getInstance.getTime, notes))
      // Transaction -> TransactionForm
      ((t: Transaction) => Some(t.symbol, t.ttype, t.price.toDouble, t.quantity, t.notes))
)
  
  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  def list = Action.async {
    val transactionsAsFuture = scala.concurrent.Future{ Transaction.getAll }
    transactionsAsFuture.map(transactions => Ok(Json.toJson(transactions)))
  }

  /**
   * The Sencha client will send me id, symbol, and companyName in a POST request.
   * I need to return something like this on success:
   *     { "success" : true, "msg" : "", "id" : 100 }
   */
  def add = Action { implicit request =>
    transactionForm.bindFromRequest.fold(
      errors => {
        Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Boom!"), "id" -> toJson(0))))
      },
      transaction => {
        val id = Transaction.insert(transaction)
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
   * Delete a transaction, asynchronously.
   */
  def delete(id: Long) = Action.async {
    val futureNumRowsDeleted = scala.concurrent.Future{ Transaction.delete(id) }
    // TODO handle the case where 'count < 1' properly 
    futureNumRowsDeleted.map{ count =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Transaction was deleted"), "id" -> toJson(count))
        Ok(Json.toJson(result))
    }
  }
  

}








