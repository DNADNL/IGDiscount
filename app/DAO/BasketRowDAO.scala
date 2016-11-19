package DAO

import com.avaje.ebean.{Ebean, Expr}
import models.{BasketRow, Product, SimpleUser}

import scala.collection.JavaConverters._

/**
  * Created by kevin on 16/11/16.
  */
class BasketRowDAO extends DAO(classOf[BasketRow], classOf[Long]) {

  def findAllBasket(su : SimpleUser): List[BasketRow] = Ebean.find(classOf[BasketRow]).where(Expr.eq("simpleUser",su)).findList().asScala.toList

  def findByProduct(product : Product) : Option[BasketRow] = {
    Ebean.find(classOf[BasketRow]).where(Expr.eq("product", product)).findUnique() match {
      case br => Some(br)
      case null => None
    }
  }

}
