package controllers

import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

/**
 * This code assumes that a `username` is stored in the Play Framework session.
 * Usage: 
 * 
 *    def currentUser = Authenticated { request =>
 *        Ok("The current user is " + request.username)
 *    }
 *
 */
object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      // note: i used to check `username` here; now check `uuid`
      request.session.get("uuid").map { uuid =>
          println("AuthenticatedAction - USER IS OKAY")
          block(new AuthenticatedRequest(uuid, request))
      } getOrElse {
          println("AuthenticatedAction - NOT AUTHENTICATED")
          Future.successful(Forbidden)
      }
  }

}

