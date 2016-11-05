package DAO

import com.avaje.ebean.{Ebean, Expr}
import com.avaje.ebean.Model.Finder
import models.models.SellerCompany
import models.{Admin, SimpleUser, Token}

/**
  * Created by kevin on 24/10/16.
  */
abstract class SimpleUserDAO extends DAO(classOf[SimpleUser], classOf[Long]) with UserDAO[SimpleUser] {

  override def findByLogin(email : String, psw : String) : Option[SimpleUser] = {
    Ebean.find(classOf[SimpleUser]).where(Expr.and(Expr.eq("email", email), Expr.eq("password", psw))).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  def findByEmail(email : String) : Option[SimpleUser] = {
    Ebean.find(classOf[SimpleUser]).where(Expr.eq("email", email)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def updatePassword(su : SimpleUser) : Boolean = {
    super.find(su.id) match {
      case Some(x) => {
        x.password = su.password
        super.update(x)
      }
      case _ => false
    }
  }

  override def updateWithoutPassword(su: SimpleUser) : Boolean = {
    super.find(su.id) match {
      case Some(x) => {
        su.password = x.password
        super.update(su)
      }
      case _ => false
    }
  }

  override def finByTokenHash(token: Token) : Option[SimpleUser] = {
    Ebean.find(classOf[SimpleUser]).where(Expr.eq("tokenAuthentification", token)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def tokenConform(token: Token, su : SimpleUser) : Boolean = {
    this.finByTokenHash(token) match {
      case Some(x) => x.id.equals(su.tokenAuthentification.id)
      case _ => false
    }
  }

  override def tokenConform(tokenHash: String, su : SimpleUser) : Boolean = {
    Token.findByToken(tokenHash) match {
      case Some(x) => this.finByTokenHash(x) match {
        case Some(y) => y.tokenAuthentification.id.equals(su.tokenAuthentification.id)
        case _ => false
      }
      case _ => false
    }
  }

  override def updateTokenAuthentification(su : SimpleUser, t : Token) : Boolean = {
    su.tokenAuthentification = t
    super.update(su)
  }

}
