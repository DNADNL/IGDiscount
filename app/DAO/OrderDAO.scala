package DAO

import com.avaje.ebean.{Ebean, Expr}
import models.Order
import models.models.SellerCompany

import scala.collection.JavaConverters._

/**
  * Created by kevin on 18/11/16.
  */
class OrderDAO extends DAO(classOf[Order], classOf[Long]){

  def findAll(state : String): List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.eq("state",state)).findList().asScala.toList
  }

  def findAll(sellerCompany : SellerCompany) : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.eq("product.sellerCompany",sellerCompany)).findList().asScala.toList
  }

}
