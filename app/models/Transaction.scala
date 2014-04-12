package models

import java.text.SimpleDateFormat

case class Transaction (
    id: Long, 
    uid: Long, 
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
  // `price` field comes back from the jdbc driver as a `java.math.BigDecimal`, so convert it to a 
  // scala BigDecimal as needed.
  val transaction = {
    get[Long]("id") ~ 
    get[Long]("uid") ~ 
    get[String]("symbol") ~ 
    get[String]("ttype") ~
    get[java.math.BigDecimal]("price") ~
    get[Int]("quantity") ~
    get[java.util.Date]("date_time") ~ 
    get[String]("notes") map {
      case id~uid~symbol~ttype~price~quantity~datetime~notes => Transaction(id, uid, symbol, ttype, BigDecimal(price), quantity, datetime, notes)
    }
  }

  // TODO add 'order by'
  def getAll(): List[Transaction] = DB.withConnection { implicit c =>
    SQL("select * from transactions order by date_time desc").as(transaction *)
  } 

  /**
   * This method returns the value of the auto_increment field when the transaction is inserted
   * into the database table.
   * 
   * Note: Inserting `transaction.price` does not work, throws nasty exception; need to insert a Java BigDecimal.
   */
  def insert(transaction: Transaction): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into transactions (uid, symbol, ttype, price, quantity, notes) values ({uid}, {symbol}, {ttype}, {price}, {quantity}, {notes})")
        .on("uid" -> transaction.uid,
            "symbol" -> transaction.symbol.toUpperCase,
            "ttype" -> transaction.ttype,
            "price" -> transaction.price.bigDecimal,  //converts to java.math.BigDecimal
            "quantity" -> transaction.quantity,
            "notes" -> transaction.notes
        ).executeInsert()
      }
    id
  }

  /**
   * Delete a transaction given its `id`.
   * TODO add `uid` here
   */
  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM transactions WHERE id = {id}")
        .on('id -> id)
        .executeUpdate()
      nRowsDeleted
    }
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
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val transactionSeq = Seq(
              "id" -> JsNumber(transaction.id),
              "uid" -> JsNumber(transaction.uid),
              "symbol" -> JsString(transaction.symbol),
              "ttype" -> JsString(transaction.ttype),
              "price" -> JsNumber(transaction.price),
              "quantity" -> JsNumber(transaction.quantity),
              "datetime" -> JsString(sdf.format(transaction.datetime)),
              "notes" -> JsString(transaction.notes)
          )
          JsObject(transactionSeq)
      }

      // convert from a JSON string to a Transaction object (de-serializing from JSON)
      def reads(json: JsValue): JsResult[Transaction] = {
          val id = (json \ "id").as[Long]
          val uid = (json \ "uid").as[Long]
          val symbol = (json \ "symbol").as[String]
          val ttype = (json \ "ttype").as[String]
          val price = (json \ "price").as[BigDecimal]
          val quantity = (json \ "quantity").as[Int]
          val datetime = (json \ "datetime").as[java.util.Date]
          val notes = (json \ "notes").as[String]
          JsSuccess(Transaction(id, uid, symbol, ttype, price, quantity, datetime, notes))
      }
  }

}







