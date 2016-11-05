package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SimpleUser, Token}
import models.models.SellerCompany
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller, Result}
import play.api.libs.json.Json
import play.mvc.Results
import views.html.defaultpages.notFound

/**
  * Created by kevin on 29/10/16.
  */
@Singleton
class RegistrationUserController @Inject() extends Controller {

  val parameterRegistrationSimpleUser = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "postalCode" -> number,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  val parameterRegistrationSellerCompany = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "postalCode" -> number,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> number,
      "siret" -> bigDecimal,
      "companyName" -> nonEmptyText
    )
  )

  val parameterRegistrationAdmin = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  val jsonErrorForm = Json.obj(
    "error" -> true,
    "message" -> "Parameters error"
  )

  val jsonErrorUserExist = Json.obj(
    "error" -> true,
    "message" -> "User already exists"
  )

  val jsonUserCreated = Json.obj(
    "error" -> false,
    "message" -> "User created"
  )

  val jsonRequiredAdminCreateAdmin = Json.obj(
    "error" -> true,
    "message" -> "You cannot create admin"
  )

  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  def registrationSimpleUser = Action { implicit request =>
    parameterRegistrationSimpleUser.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, password, postalCode, street, city, streetNumber, firstName, lastName) = formData
        val su = SimpleUser(email, password, postalCode.toString, street, city, streetNumber.toString, firstName, lastName)
        SimpleUser.save(su) match {
          case true => Created(jsonUserCreated)
          case false => Conflict(jsonErrorUserExist)
        }
      }
    )
  }

  def registrationSellerCompany = Action { implicit request =>
    println(parameterRegistrationSellerCompany.bindFromRequest().errors)
    parameterRegistrationSellerCompany.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, password, postalCode, street, city, streetNumber, siret, companyName) = formData
        val sc = SellerCompany(email, password, postalCode.toString, street, city, streetNumber.toString, siret.toString, companyName)
        SellerCompany.save(sc) match {
          case true => Created(jsonUserCreated)
          case false => Conflict(jsonErrorUserExist)
        }
      }
    )
  }

  def registrationAdmin = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => parameterRegistrationAdmin.bindFromRequest.fold(
            formWithErrors => BadRequest(jsonErrorForm),
            formData => {
              val (email, password, firstName, lastName) = formData
              val a = Admin(email, password, firstName, lastName)
              Admin.save(a) match {
                case true => Created(jsonUserCreated)
                case false => Conflict(jsonErrorUserExist)
              }
            }
          )
          case _ => Unauthorized(jsonRequiredAdminCreateAdmin)
        }
        case false => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

}