package controllers

import play.api._
import play.api.cache.Cache
import play.api.Play.current  // bring implicit Application into scope; needed for Cache
import play.api.mvc._

trait BaseControllerTrait {

  /**
   * The Cache will contain a map of (uuid -> uid).
   * To retrieve it, (a) get the uuid from the session (as a String), 
   * then (b) get the uid from the cache like this:
   * 
   * val uidOption: Option[Long] = Cache.getAs[Long](uuid)
   * val uid = Cache.getOrElse[Long](uuid){-1}
   * val uid = Cache.getOrElse[Long](uuid)(-1)  // not sure about syntax
   * 
   */
  def putUidInCache(uuid: String, uid: Long) {
    Cache.set(uuid, uid)
  }

  /**
   * Attempt to get the `uid` from the Play cache, based on the 
   * `uuid` in the Play session.
   */
  def getUid(session: Session): Option[Long] = {
    // get the uuid from the session, then get the uid from the cache
    println("*getting the uid from the session*")
    session.get("uuid") match {
      case None => None
      case Some(uuid) => Cache.getAs[Long](uuid)
    }
  }

  def getUuid = java.util.UUID.randomUUID.toString

}










