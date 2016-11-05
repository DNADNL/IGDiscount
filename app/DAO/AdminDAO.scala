package DAO

import java.util.UUID

import com.avaje.ebean.{Ebean, Expr}
import models.{Admin, SimpleUser, Token}
import models.models.SellerCompany

/**
  * Created by kevin on 30/10/16.
  */
class AdminDAO extends DAO(classOf[Admin], classOf[Long]) with UserDAO[Admin] {

  override def findByLogin(email : String, psw : String) : Option[Admin] = {
    Ebean.find(classOf[Admin]).where(Expr.and(Expr.eq("email", email), Expr.eq("password", psw))).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def updatePassword(a : Admin) : Boolean = {
    super.find(a.id) match {
      case Some(x) => {
        x.password = a.password
        super.update(x)
      }
      case _ => false
    }
  }

  override def updateWithoutPassword(a: Admin) : Boolean = {
    super.find(a.id) match {
      case Some(x) => {
        a.password = x.password
        val email = a.email
        super.update(a)
      }
      case _ => false
    }
  }

  override def updateTokenAuthentification(a : Admin, t : Token) : Boolean = {
    a.tokenAuthentification = t
    super.update(a)
  }

  override def finByTokenHash(token: Token) : Option[Admin] = {
    Ebean.find(classOf[Admin]).where(Expr.eq("tokenAuthentification", token)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def tokenConform(token: Token, a : Admin) : Boolean = {
    this.finByTokenHash(token) match {
      case Some(x) => x.id.equals(a.tokenAuthentification.id)
      case _ => false
    }
  }

  override def tokenConform(tokenHash: String, a : Admin) : Boolean = {
    Token.findByToken(tokenHash) match {
      case Some(x) => this.finByTokenHash(x) match {
        case Some(y) => y.tokenAuthentification.id.equals(a.tokenAuthentification.id)
        case _ => false
      }
      case _ => false
    }
  }

}

