package models

import java.security.SecureRandom
import java.sql.Timestamp
import java.util.{Calendar, Date, UUID}
import javax.persistence._

import DAO.{SimpleUserDAO, TokenDAO}
import com.avaje.ebean.Model
import com.avaje.ebean.Model.Finder
import org.h2.engine.User
import play.api.libs.json.{Json, Writes}

import scala.util.Random

/**
  * Created by kevin on 30/10/16.
  */
@Entity
class Token extends Model {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  var expirationDate : Timestamp = _
  @Column(unique=true)
  var token : String =_

}

object Token extends TokenDAO {

  def apply(): Token = {
    val t = new Token()
    t.token = UUID.randomUUID().toString()
    val tomorrow = Calendar.getInstance()
    tomorrow.add(Calendar.DATE, 1)
    t.expirationDate = new Timestamp(tomorrow.getTime.getTime)
    t
  }

  def isValid(t : Token) : Boolean = {
    t.expirationDate.after(new Timestamp(new Date().getTime))
  }

  def isValid(token : String) : Boolean = {
    this.findByToken(token) match {
      case Some(t) => this.isValid(t)
      case _ => false
    }
  }

  implicit val taskWrites = new Writes[Token] {
    def writes(t: Token) = Json.obj(
      "id" -> t.id,
      "expirationDate" -> t.expirationDate,
      "token" -> t.token
    )
  }

}


