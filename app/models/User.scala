package models

case class User (
    val id: Long, 
    var name: String,      // "Alvin Alexander" 
    var username: String,  // "alvin"
    var password: String,
    var email: String
)

object User {
  
  import anorm._
  import anorm.SqlParser._
  import play.api.db._
  import play.api.Play.current

  // a parser that will transform a JDBC ResultSet row to a Stock value
  // uses the Parser API
  // http://www.playframework.org/documentation/2.0/ScalaAnorm
  // these names need to match the field names in the 'stocks' database table
  val user = {
    get[Long]("id") ~ 
    get[String]("name") ~ 
    get[String]("username") ~
    get[String]("password") ~
    get[String]("email") map {
      case id~name~username~password~email => User(id, name, username, password, email)
    }
  }

  /**
   * Returns true if one record is found in the users table with the given username and password.
   */
  def userIsInDatabase(username: String, password: String): Boolean = DB.withConnection { implicit c =>
    val numRecs = SQL("select * from users where username = {username} and password = {password}")
        .on('username -> username, 'password -> password)
        .as(user *)
    (numRecs.size == 1)
  }

  /**
   * Look up the `uid` for the given username and password.
   * TODO return an Option.
   * TODO need to return a None when the user is not found.
   */
  def getUserId(username: String, password: String): Option[Long] = DB.withConnection { implicit c =>
    // TODO handle the case where this returns nothing
    val rowOption = SQL("select id from users where username = {username} and password = {password} limit 1")
        .on('username -> username, 'password -> password)
        .apply
        .headOption
    rowOption match {
      case Some(row) => Some(row[Long]("id"))  // the uid
      case None => None
    }
  }
  

}










