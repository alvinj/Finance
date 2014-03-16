package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer

object Stocks extends Controller {

  val stockForm: Form[Stock] = Form(
    // defines a mapping that will handle Stock values
    mapping(
      // verifying here creates a field-level error
      // if your test returns false, the error is shown
      "symbol" -> nonEmptyText.verifying("Doh - Stock already exists (1)!", Stock.findBySymbol(_) == 0),
      "company" -> nonEmptyText)
      ((symbol, company) => Stock(0, symbol, company))
      ((s: Stock) => Some((s.symbol, s.company)))
      verifying("Doh - Stock already exists (2)!", fields => fields match {
        // this block creates a 'form' error; trying to display it in the template
        // this only gets called if all field validations are okay
        case Stock(i, s, c) =>  Stock.findBySymbol(s) == 0
      })
  )
  def list = Action {
    val stocks = Stock.getAll2
    val result = "<pre>" + stocks.mkString(",") + "</pre>"
    Ok(result)
  }

//  def add = Action { implicit request =>
//    stockForm.bindFromRequest.fold(
//      errors => BadRequest(html.stock.form(errors)),  // back to form
//      stock => {
//        println("*** CAME TO STOCK AREA (WRONG) ***")
//        Stock.create(stock)
//        Redirect(routes.Stocks.list)
//      }
//    )
//  }
  
  
}





