package models

import javax.persistence._

import DAO.BasketRowDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Created by kevin on 16/11/16.
  */
@Entity
@UniqueConstraint(columnNames = Array("product_id", "simple_user_id"))
class BasketRow extends Model {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  var quantity : Int =_
  @ManyToOne
  var product : Product =_
  @ManyToOne
  var simpleUser : SimpleUser =_

}

object BasketRow extends BasketRowDAO {

  def apply(product : Product, simpleUser : SimpleUser, qty : Int) : BasketRow = {
    val br = new BasketRow()
    br.product = product
    br.simpleUser = simpleUser
    br.quantity = qty
    return br
  }

  implicit val taskWrites = new Writes[BasketRow] {
    def writes(br: BasketRow) = Json.obj(
      "id" -> br.id,
      "quantity" -> br.quantity,
      "product" -> br.product,
      "user" -> br.simpleUser
    )
  }
}
