package DAO

import models.Product
import scala.collection.JavaConverters._

/**
  * Created by kevin on 05/11/16.
  */
class ProductDAO extends DAO(classOf[Product], classOf[Long]) {

  /*override def findAll(): List[Product] = {
    super.finder.fetch().findList().asScala.toList
  }*/
}
