package DAO

import com.avaje.ebean.{Ebean, Expr}
import controllers.UserIdentification
import models.{Admin, Token}

import scala.reflect.ClassTag

/**
  * Created by kevin on 03/11/16.
  */
trait UserDAO[T <: UserIdentification ] {

  def findByLogin(email : String, psw : String) : Option[T]
  def updateTokenAuthentification(u : T, t : Token) : Boolean
  def finByTokenHash(token: Token) : Option[T]
  def tokenConform(token: Token, u : T) : Boolean
  def tokenConform(tokenHash: String, u : T) : Boolean
  def updateWithoutPassword(u: T) : Boolean
  def updatePassword(u : T) : Boolean

}
