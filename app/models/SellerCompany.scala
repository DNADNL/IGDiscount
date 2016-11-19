package models

/**
  * Created by kevin on 30/10/16.
  */
import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.SellerCompanyDAO
import com.avaje.ebean.Model
import com.google.common.hash.Hashing
import controllers.{UserAdress, UserIdentification}
import play.api.libs.json.{Json, Writes}

/**
  * Created by kevin on 24/10/16.
  */

@Entity
case class SellerCompany() extends Model with UserIdentification with UserAdress
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
  var siret : String =_
  var companyName : String =_

  @OneToOne
  var tokenAuthentification : Token =_
  @OneToOne
  var tokenReinitialisationEmail : Token =_
  @OneToMany(cascade = Array(CascadeType.ALL))
  var products : java.util.List[Product] =_
}

object SellerCompany extends SellerCompanyDAO {

  def apply(
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             siret: String,
             companyName: String): SellerCompany = {
    val sc = new SellerCompany()
    sc.email = email
    sc.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    sc.postalCode = postalCode
    sc.street = street
    sc.city = city
    sc.streetNumber = streetNumber
    sc.siret = siret
    sc.companyName = companyName
    return sc
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             siret: String,
             companyName: String): SellerCompany = {
    val sc = new SellerCompany()
    sc.id = id
    sc.email = email
    sc.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    sc.postalCode = postalCode
    sc.street = street
    sc.city = city
    sc.streetNumber = streetNumber
    sc.siret = siret
    sc.companyName = companyName
    return sc
  }

  implicit val taskWrites = new Writes[SellerCompany] {
    def writes(sc: SellerCompany) = Json.obj(
      "id" -> sc.id,
      "email" -> sc.email,
      "postalCode" -> sc.postalCode,
      "street" -> sc.street,
      "streetNumber" -> sc.streetNumber,
      "city" -> sc.city,
      "siret" -> sc.siret,
      "companyName" -> sc.companyName
    )
  }

}

