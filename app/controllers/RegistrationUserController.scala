package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SimpleUser, Token}
import models.models.SellerCompany
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller, Cookie, Result}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Results
import views.html.defaultpages.notFound

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by kevin on 29/10/16.
  */
@Singleton
class RegistrationUserController @Inject() (ws: WSClient, configuration: Configuration) extends Controller {

  val parameterRegistrationSimpleUser = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "postalCode" -> nonEmptyText(minLength = 5),
      "street" -> nonEmptyText(minLength = 1),
      "city" -> nonEmptyText(minLength = 1),
      "streetNumber" -> nonEmptyText(minLength = 1),
      "firstName" -> nonEmptyText(minLength = 1),
      "lastName" -> nonEmptyText(minLength = 1)
    )
  )

  val parameterRegistrationSimpleUserFacebook = Form(
    tuple(
      "tokenFacebook" -> nonEmptyText,
      "postalCode" -> nonEmptyText(minLength = 5),
      "street" -> nonEmptyText(minLength = 1),
      "city" -> nonEmptyText(minLength = 1),
      "streetNumber" -> nonEmptyText(minLength = 1),
      "firstName" -> nonEmptyText(minLength = 1),
      "lastName" -> nonEmptyText(minLength = 1)
    )
  )

  val parameterRegistrationSellerCompany = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "postalCode" -> nonEmptyText(minLength = 5),
      "street" -> nonEmptyText(minLength = 1),
      "city" -> nonEmptyText(minLength = 1),
      "streetNumber" -> nonEmptyText(minLength = 1),
      "siret" -> nonEmptyText(minLength = 14),
      "companyName" -> nonEmptyText(minLength = 1)
    )
  )

  val parameterRegistrationAdmin = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5),
      "firstName" -> nonEmptyText(minLength = 1),
      "lastName" -> nonEmptyText(minLength = 1)
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

  val jsonFacebookError =Json.obj(
    "error" -> true,
    "message" -> "Facebook failed"
  )

  def registrationSimpleUser = Action { implicit request =>
    parameterRegistrationSimpleUser.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, password, postalCode, street, city, streetNumber, firstName, lastName) = formData
        val su = SimpleUser(email, password, postalCode, street, city, streetNumber, firstName, lastName)
        SimpleUser.save(su) match {
          case true => Created(jsonUserCreated)
          case false => Conflict(jsonErrorUserExist)
        }
      }
    )
  }

  def registrationSimpleUserFacebook = Action { implicit request =>
    parameterRegistrationSimpleUserFacebook.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (tokenFacebook, postalCode, street, city, streetNumber, firstName, lastName) = formData
        val futureResultEmail: Future[WSResponse] = ws.url("https://graph.facebook.com/me").withQueryString("fields" -> "email").withQueryString("access_token" -> tokenFacebook).get()
        val futureResultId: Future[WSResponse] = ws.url("https://graph.facebook.com/app").withQueryString("fields" -> "id").withQueryString("access_token" -> tokenFacebook).get()

        val jsonEmail = Await.result(futureResultEmail, Duration.Inf).json
        val jsonId = Await.result(futureResultId, Duration.Inf).json
        (jsonId \ ("id")).toOption match {
          case Some(publicKey) => {
            (jsonEmail \ ("email")).toOption match {
              case Some(emailRes) => {
                val su = SimpleUser(emailRes.toString().replace("\"", ""), "", postalCode, street, city, streetNumber, firstName, lastName)
                su.logFacebook = true
                su.save()
                Created(jsonUserCreated)
              }
              case _ => NotFound(jsonFacebookError)
            }
          }
          case _ => NotFound(jsonFacebookError)
        }
      }
    )
  }

  def registrationSellerCompany = Action { implicit request =>
    parameterRegistrationSellerCompany.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, password, postalCode, street, city, streetNumber, siret, companyName) = formData
        val sc = SellerCompany(email, password, postalCode, street, city, streetNumber, siret, companyName)
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