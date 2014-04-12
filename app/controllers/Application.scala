package controllers

import play.api._
import play.api.cache.Cache
import play.api.Play.current  // bring implicit Application into scope; needed for Cache
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import models.User

object Application extends Controller with BaseControllerTrait {

    // [id: Long, name: String, username: String, password: String, email: String]
    val loginForm: Form[User] = Form(
    // the names you use in this mapping (such as 'symbol') must match the names that will be
    // POSTed to your methods in JSON.
    mapping(
        // verifying here creates a field-level error; if your test returns false, the error is shown
        "username" -> nonEmptyText,
        "password" -> nonEmptyText
      )
      ((username, password) => User(0, "", username, password, ""))  // userForm -> User
      ((u: User) => Some(u.username, u.password))  // User -> UserForm
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  /**
   * -----
   * CACHE
   * -----
   * 
   * // can also set the duration; see http://www.playframework.com/documentation/2.2.x/api/scala/index.html#play.api.cache.Cache$
   * Cache.set("item.key", connectedUser)
   * 
   * val maybeUser: Option[User] = Cache.getAs[User]("item.key")
   * 
   * val user: User = Cache.getOrElse[User]("item.key") {
   *   User.findById(connectedUser)
   * }
   * 
   * Cache.remove("item.key")
   * 
   * Some session handling: http://stackoverflow.com/questions/20772841/play-framework-handling-session-state
   * 
   */


  /**
   * Note that this is a regular Action, not an AuthenticatedAction.
   */
  def login = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => {
        // data sent did not validate
        NotFound(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Bad login data"), "id" -> toJson(0))))
      },
      user => {
        val uidOption = User.getUserId(user.username, user.password)
        uidOption match {
          case None => 
               NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Invalid username/password combo"), "id" -> toJson(0))))
          case Some(uid) =>
               // TODO i'm jumping thru some hoops here with a uuid because i can't easily get the session token
               val uuid = getUuid
               putUidInCache(uuid, uid)
               Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Welcome"), "id" -> toJson(0))))
                 .withSession("username" -> user.username, "authenticated" -> "yes", "uuid" -> uuid)
        }
      }
    )
  }

  /**
   * @see http://www.playframework.com/documentation/2.2.x/ScalaSessionFlash
   * @note `withNewSession` destroys the old session
   */
  def logout = Action { implicit request =>
    session.get("uuid") foreach { uuid =>
      Cache.remove(uuid)
    }
    Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("You are logged out")))).withNewSession
  }

}







