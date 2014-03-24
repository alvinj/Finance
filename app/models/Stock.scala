package models

case class Stock (val id: Long, 
                  var symbol: String, 
                  var companyName: String)

object Stock {
  
  import anorm._
  import anorm.SqlParser._
  import play.api.db._
  import play.api.Play.current

  // a parser that will transform a JDBC ResultSet row to a Stock value
  // uses the Parser API
  // http://www.playframework.org/documentation/2.0/ScalaAnorm
  // these names need to match the field names in the 'stocks' database table
  val stock = {
    get[Long]("id") ~ 
    get[String]("symbol") ~ 
    get[String]("company") map {
      case id~symbol~company => Stock(id, symbol, company)
    }
  }
  
  /**
   * SELECT * (VERSION 2)
   * This will only work with Play libraries in scope.
   * -------------------------------------------------------------------------------------
   */
  import anorm._
  import anorm.SqlParser._

  import play.api.db._
  import play.api.Play.current

  def getAll2(): List[Stock] = DB.withConnection { implicit c =>
    SQL("select * from stocks order by symbol asc").as(stock *)
  }
  
//  // .on("countryCode" -> "FRA")
//  def create(stock: Stock): Int = {
//    DB.withConnection { implicit c =>
//      val result = SQL("insert into stocks (symbol, companyName) values ({symbol}, {companyName})")
//        .on('symbol -> stock.symbol.toUpperCase,
//            'company -> stock.companyName
//        ).executeInsert()
//      result
//    }
//  }

  /**
   * This method returns the value of the auto_increment field when the stock is inserted
   * into the database table.
   */
  def insert(stock: Stock): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into stocks (symbol, company) values ({symbol}, {companyName})")
        .on("symbol" -> stock.symbol.toUpperCase,
            "companyName" -> stock.companyName)
        .executeInsert()
      }
    id
  }
  
  def update(id: Long, stock: Stock) {
    DB.withConnection { implicit c =>
      SQL("update stocks set symbol={symbol}, company={companyName} where id={id})")
      .on('symbol -> stock.symbol,
          'company -> stock.companyName,
          'id -> id
      ).executeUpdate()
    }
  }
  
  /**
   * Used this method to see what the different return types are.
   */
  def poop(symbol: String) {
    DB.withConnection { implicit c =>
      // (1) firstRow is anorm.SqlQuery
      val firstRow = SQL("SELECT COUNT(*) AS c FROM stocks WHERE symbol = {symbol}")
      println("1: " + firstRow.getClass)

      // (2) anorm.SimpleSql
      val poop = SQL("SELECT COUNT(*) AS c FROM stocks WHERE symbol = {symbol}")
        .on('symbol -> symbol.toUpperCase)
      println("2: " + poop.getClass)

      // (3) scala.collection.immutable.Stream$Cons
      val poop2 = SQL("SELECT COUNT(*) AS c FROM stocks WHERE symbol = {symbol}")
        .on('symbol -> symbol.toUpperCase)
        .apply
      println("3: " + poop2.getClass)
    }
  }

  /**
   * Returns a count of how many times the given `symbol` is found in the stocks table.
   */
  def findBySymbol(symbol: String): Long = {
    if (symbol.trim.equals("")) return 0
    println("\n>>>>> findBySymbol called with: " + symbol)
    DB.withConnection { implicit c =>
      // firstRow is anorm.SqlRow
      val firstRow = SQL("SELECT COUNT(*) AS c FROM stocks WHERE symbol = {symbol}")
        .on('symbol -> symbol.toUpperCase)
        .apply
        .head
      val count = firstRow[Long]("c")
      println(s"count = $count")
      count
    }
  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM stocks WHERE id = {id}")
        .on('id -> id)
        .executeUpdate()
      nRowsDeleted
    }
  }

  def delete(symbol: String): Int = {
    DB.withConnection { implicit c =>
      val nRowsDeleted = SQL("DELETE FROM stocks WHERE symbol = {symbol}")
        .on('symbol -> symbol)
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

  implicit object StockFormat extends Format[Stock] {

      // convert from Stock object to JSON (serializing to JSON)
      def writes(stock: Stock): JsValue = {
          val stockSeq = Seq(
              "id" -> JsNumber(stock.id),
              "symbol" -> JsString(stock.symbol),
              "companyName" -> JsString(stock.companyName))
          JsObject(stockSeq)
      }

      // convert from a JSON string to a Stock object (de-serializing from JSON)
      def reads(json: JsValue): JsResult[Stock] = {
          val id = (json \ "id").as[Long]
          val symbol = (json \ "symbol").as[String]
          val companyName = (json \ "companyName").as[String]
          JsSuccess(Stock(id, symbol, companyName))
      }

  }

}








