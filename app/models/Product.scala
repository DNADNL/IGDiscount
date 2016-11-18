package models

import java.sql.Blob
import javax.persistence._

import DAO.{AdminDAO, ProductDAO}
import com.avaje.ebean.Model
import controllers.{UserIdentification, UserName}
import play.api.libs.json.{Json, Writes}
import models.SellerCompany

/**
  * Created by kevin on 05/11/16.
  */
@Entity
case class Product() extends Model {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  @Column(length=1000)
  var description : String =_
  var name : String =_
  var price : Float =_
  var quantity : Long =_
  var available : Boolean=_

  @ManyToOne
  var sellerCompany : SellerCompany =_
  @OneToOne(cascade = Array(CascadeType.ALL))
  var image : Image =_
  @OneToMany(cascade = Array(CascadeType.ALL))
  var basketRow : java.util.List[BasketRow] =_
  @OneToMany(cascade = Array(CascadeType.ALL))
  var orders : java.util.List[BasketRow] =_

}

object Product extends ProductDAO {

  def apply(
             description: String,
             name: String,
             price: Float,
             quantity: Long,
             seller : SellerCompany): Product = {
    val p = new Product()
    p.description = description
    p.available = true
    p.name = name
    p.price = price
    p.quantity = quantity
    p.sellerCompany = seller
    return p
  }

  def apply(
             id: Long,
             description: String,
             name: String,
             price: Float,
             quantity: Long,
             seller : SellerCompany): Product = {
    val p = new Product()
    p.id = id
    p.description = description
    p.name = name
    p.available = true
    p.price = price
    p.quantity = quantity
    p.sellerCompany = seller
    return p
  }

  implicit val taskWrites = new Writes[Product] {
    def writes(p: Product) = Json.obj(
      "id" -> p.id,
      "description" -> p.description,
      "name" -> p.name,
      "price" -> p.price,
      "quantity" -> p.quantity,
      "seller" -> p.sellerCompany,
      "image" -> p.image,
      "available" -> p.available
    )
  }

}
