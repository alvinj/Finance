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

  def list = Action {
    val stocks = Stock.getAll2
    val result = "<pre>" + stocks.mkString(",") + "</pre>"
    Ok(result)
  }
  
  
}