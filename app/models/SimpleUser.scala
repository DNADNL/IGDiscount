package models

import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.SimpleUserDAO
import com.avaje.ebean.{Ebean, Model}
import com.avaje.ebean.Model.Finder
import com.google.common.hash.Hashing
import controllers.{UserAdress, UserIdentification, UserName}
import play.api.libs.json.{Json, Writes}

import scala.collection.JavaConverters._

/**
  * Created by kevin on 24/10/16.
  */

@Entity
case class SimpleUser() extends Model with UserIdentification with UserAdress with UserName
{

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  @Column(unique=true)
  var email : String =_
  var password : String =_
  var postalCode : String =_
  var street : String =_
  var city : String =_
  var streetNumber : String =_
  var firstName : String =_
  var lastName : String =_
  var logFacebook : Boolean =_

  @OneToOne
  var tokenAuthentification : Token =_
  @OneToOne
  var tokenReinitialisationEmail : Token =_
  @OneToMany(cascade = Array(CascadeType.ALL))
  var basketRow : java.util.List[BasketRow] =_
  @OneToMany(cascade = Array(CascadeType.ALL))
  var orders : java.util.List[BasketRow] =_

}

object SimpleUser extends SimpleUserDAO {

  def apply(
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             firstName: String,
             lastName: String): SimpleUser = {
    val u = new SimpleUser()
    u.email = email
    u.logFacebook = false
    u.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    u.postalCode = postalCode
    u.street = street
    u.city = city
    u.streetNumber = streetNumber
    u.firstName = firstName
    u.lastName = lastName
    return u
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             firstName: String,
             lastName: String): SimpleUser = {
    val u = new SimpleUser()
    u.id = id
    u.logFacebook = false
    u.email = email
    u.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    u.postalCode = postalCode
    u.street = street
    u.city = city
    u.streetNumber = streetNumber
    u.firstName = firstName
    u.lastName = lastName
    return u
  }

  implicit val taskWrites = new Writes[SimpleUser] {
    def writes(su: SimpleUser) = Json.obj(
      "id" -> su.id,
      "email" -> su.email,
      "postalCode" -> su.postalCode,
      "street" -> su.street,
      "streetNumber" -> su.streetNumber,
      "city" -> su.city,
      "firstName" -> su.firstName,
      "lastName" -> su.lastName,
      "logFacebook" -> su.logFacebook
    )
  }

}
