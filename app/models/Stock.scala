package models

case class Stock (val id: Long, 
                  var symbol: String, 
                  var company: String)

object Stock {
  
  import anorm._
  import anorm.SqlParser._
  import play.api.db._
  import play.api.Play.current

  // a parser that will transform a JDBC ResultSet row to a Stock value
  // uses the Parser API
  // http://www.playframework.org/documentation/2.0/ScalaAnorm
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
  // method requires 'val stock' to be defined
  def getAll2(): List[Stock] = DB.withConnection { implicit c =>
    SQL("select * from stocks order by symbol asc").as(stock *)
  }
  
  // .on("countryCode" -> "FRA")
  def create(stock: Stock) {
    DB.withConnection { implicit c =>
      SQL("insert into stocks (symbol, company) values ({symbol}, {company})")
      .on('symbol -> stock.symbol.toUpperCase,
          'company -> stock.company
      ).executeUpdate()
    }
  }

  def update(id: Long, stock: Stock) {
    DB.withConnection { implicit c =>
      SQL("update stocks set symbol={symbol}, company={company} where id={id})")
      .on('symbol -> stock.symbol,
          'company -> stock.company,
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

  def findBySymbol(symbol: String): Long = {
    if (symbol.trim.equals("")) return 0
    println("\n>>>>> findBySmbol called with: " + symbol)
    DB.withConnection { implicit c =>
      // firstRow is anorm.SqlRow
      val firstRow = SQL("SELECT COUNT(*) AS c FROM stocks WHERE symbol = {symbol}")
        .on('symbol -> symbol.toUpperCase)
        .apply
        .head
      val n = firstRow[Long]("c")
      println("     n = " + n)
      n
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
  
  

//  def create(symbol: String, company: String) {
//    DB.withConnection { implicit c =>
//      SQL("insert into stocks (symbol, company) values ({symbol}, {company})")
//      .on('symbol -> symbol,
//          'company -> company
//      ).executeUpdate()
//    }
//  }
  

}



