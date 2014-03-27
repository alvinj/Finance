package models

case class Transaction (
    id: Long, 
    symbol: String,
    ttype: String,
    price: BigDecimal,
    quantity: Int,
    datetime: java.util.Date,
    notes: String
)

object Transaction {

  import anorm._
  import anorm.SqlParser._
  import play.api.db._
  import play.api.Play.current

  // a parser that will transform a JDBC ResultSet row to a Transaction value.
  // names in the `get` expressions need to match the database table field names.
  val transaction = {
    get[Long]("id") ~ 
    get[String]("symbol") ~ 
    get[String]("ttype") ~
    get[java.math.BigDecimal]("price") ~
    get[Int]("quantity") ~
    get[java.util.Date]("date_time") ~ 
    get[String]("notes") map {
      case id~symbol~ttype~price~quantity~datetime~notes => Transaction(id, symbol, ttype, price, quantity, datetime, notes)
    }
  }

  // TODO add 'order by'
  def getAll(): List[Transaction] = DB.withConnection { implicit c =>
    SQL("select * from transactions").as(transaction *)
  } 

  
  /**
   * JSON Serializer Code
   * --------------------
   */
  import play.api.libs.json.Json
  import play.api.libs.json._

  implicit object TransactionFormat extends Format[Transaction] {

      // convert from Transaction object to JSON (serializing to JSON)
      def writes(transaction: Transaction): JsValue = {
          val transactionSeq = Seq(
              "id" -> JsNumber(transaction.id),
              "symbol" -> JsString(transaction.symbol),
              "ttype" -> JsString(transaction.ttype),
              "price" -> JsNumber(transaction.price),
              "quantity" -> JsNumber(transaction.quantity),
              "datetime" -> JsNumber(transaction.datetime.getTime),  // TODO verify
              "notes" -> JsString(transaction.notes)
          )
          JsObject(transactionSeq)
      }

      // convert from a JSON string to a Transaction object (de-serializing from JSON)
      def reads(json: JsValue): JsResult[Transaction] = {
          val id = (json \ "id").as[Long]
          val symbol = (json \ "symbol").as[String]
          val ttype = (json \ "ttype").as[String]
          val price = (json \ "price").as[BigDecimal]
          val quantity = (json \ "quantity").as[Int]
          val datetime = (json \ "datetime").as[java.util.Date]
          val notes = (json \ "notes").as[String]
          JsSuccess(Transaction(id, symbol, ttype, price, quantity, datetime, notes))
      }
  }

}







