package models

import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.AdminDAO
import com.avaje.ebean.Model
import com.google.common.hash.Hashing
import controllers.{UserIdentification, UserName}
import play.api.libs.json.{Json, Writes}

/**
  * Created by kevin on 30/10/16.
  */
@Entity
case class Admin() extends Model with UserIdentification with UserName
{

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  @Column(unique=true)
  var email : String =_
  var password : String =_
  var firstName : String =_
  var lastName : String =_

  @OneToOne
  var tokenAuthentification : Token =_
  @OneToOne
  var tokenReinitialisationEmail : Token =_
}

object Admin extends AdminDAO {

  def apply(
             email: String,
             password: String,
             firstName: String,
             lastName: String): Admin = {
    val a = new Admin()
    a.email = email
    a.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    a.firstName = firstName
    a.lastName = lastName
    return a
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             firstName: String,
             lastName: String): Admin = {
    val a = new Admin()
    a.id = id
    a.email = email
    a.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    a.firstName = firstName
    a.lastName = lastName
    return a
  }

  implicit val taskWrites = new Writes[Admin] {
    def writes(a: Admin) = Json.obj(
      "id" -> a.id,
      "email" -> a.email,
      "password" -> a.password,
      "firstName" -> a.firstName,
      "lastName" -> a.lastName
    )
  }

}

