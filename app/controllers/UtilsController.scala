package controllers

import javax.inject.{Inject, Singleton}

import models.models.SellerCompany
import models.{Admin, SimpleUser, Token}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by kevin on 02/11/16.
  */
@Singleton
class UtilsController @Inject() extends Controller {

  val kindOfSimpleUser = Json.obj(
    "kindOfUser" -> "Simple User"
  )

  val kindOfSellerCompany = Json.obj(
    "kindOfUser" -> "Seller Company"
  )

  val kindOfAdmin = Json.obj(
    "kindOfUser" -> "Admin"
  )

  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  def kindOfUser = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => Ok(kindOfAdmin.deepMerge(Json.obj("id"-> a.id)))
          case su : SimpleUser => Ok(kindOfSimpleUser.deepMerge(Json.obj("id"-> su.id)))
          case sc : SellerCompany => Ok(kindOfSellerCompany.deepMerge(Json.obj("id"-> sc.id)))
        }
        case false => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

}
