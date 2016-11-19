package models

import java.sql.Timestamp
import java.util.Date
import javax.persistence._

import DAO.OrderDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Created by kevin on 18/11/16.
  */
@Entity
@Table(name = "orderProduct")
class Order extends Model {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  var quantity : Int =_
  var stateDate : Timestamp = _
  var state : String =_
  var priceOrder : Float=_
  @ManyToOne
  var product : Product =_
  @ManyToOne
  var simpleUser : SimpleUser =_

}

object Order extends OrderDAO {

  def apply(
           quantity : Int,
           state : String,
           priceOrder : Float,
           product : Product,
           simpleUser : SimpleUser
           ) : Order = {
    val o = new Order()
    o.quantity = quantity
    o.stateDate = new Timestamp(new Date().getTime)
    o.state = state
    o.priceOrder = priceOrder
    o.product = product
    o.simpleUser = simpleUser
    o
  }

  implicit val taskWrites = new Writes[Order] {
    def writes(o: Order) = Json.obj(
      "id" -> o.id,
      "quantity" -> o.quantity,
      "stateDate" -> o.stateDate,
      "priceOrder" -> o.priceOrder,
      "state" -> o.state,
      "product" -> o.product,
      "simpleUser" -> o.simpleUser
    )
  }

}

object OrderState {
  val PENDING = "Pending"
  val CONFIRMED_BY_SELLER = "Confirmed by seller"
  val CANCELLED_BY_SELLER = "Cancelled by seller"
  val PAID = "Paid"
  val SHIPPED = "Shipped"
}