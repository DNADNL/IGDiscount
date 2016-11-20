package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SellerCompany, SimpleUser, Token}
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{Action, Controller, Cookie}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by kevin on 30/10/16.
  */
@Singleton
class SignInController @Inject() (ws: WSClient, configuration: Configuration) extends Controller {

  val parameterSignIn = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  val jsonErrorForm = Json.obj(
    "error" -> true,
    "message" -> "Parameters error"
  )

  val jsonConnection = Json.obj(
    "error" -> false
  )

  val errorConnection = Json.obj(
    "error" -> true,
    "message" -> "Login invalid"
  )

  val kindOfSimpleUser = Json.obj(
    "kindOfUser" -> "Simple User"
  )

  val kindOfSellerCompany = Json.obj(
    "kindOfUser" -> "Seller Company"
  )

  val kindOfAdmin = Json.obj(
    "kindOfUser" -> "Admin"
  )

  val parameterSignInFacebook = Form(
    tuple(
      "email" -> nonEmptyText,
      "tokenFacebook" -> nonEmptyText
    )
  )

  def signInFacebook = Action {implicit request =>
    parameterSignInFacebook.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, tokenFacebook) = formData
        val futureResultEmail: Future[WSResponse] = ws.url("https://graph.facebook.com/me").withQueryString("fields" -> "email").withQueryString("access_token" -> tokenFacebook).get()
        val futureResultId: Future[WSResponse] = ws.url("https://graph.facebook.com/app").withQueryString("fields" -> "id").withQueryString("access_token" -> tokenFacebook).get()

        val jsonEmail = Await.result(futureResultEmail, Duration.Inf).json
        val jsonId = Await.result(futureResultId, Duration.Inf).json
        (jsonId \ ("id")).toOption match {
          case Some(publicKey) => {
            (jsonEmail \ ("email")).toOption match {
              case Some(emailRes) => {
                if (publicKey.toString() == '"'+configuration.underlying.getString("facebook.publicKey")+'"' && emailRes.toString() == '"'+email+'"' ) {
                  SimpleUser.findByEmail(email) match {
                    case None => NotFound(errorConnection)
                    case Some(su) => {
                      val t = Token()
                      Token.save(t)
                      SimpleUser.updateTokenAuthentification(su, t)
                      Ok(jsonConnection.deepMerge(kindOfSimpleUser)).withCookies(Cookie("token", t.token))
                    }
                  }
                }
                else {
                  NotFound(errorConnection)
                }
              }
              case _ => NotFound(errorConnection)
            }
          }
          case _ => NotFound(errorConnection)
        }
      }
    )
  }

  def signIn = Action { implicit request =>
    parameterSignIn.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorForm),
      formData => {
        val (email, password) = formData
        SimpleUser.findByLogin(email, password) match {
          case None => SellerCompany.findByLogin(email, password) match {
            case None => Admin.findByLogin(email, password) match {
              case None => NotFound(errorConnection)
              case Some(a) => {
                val t = Token()
                Token.save(t)
                Admin.updateTokenAuthentification(a, t)
                Ok(jsonConnection.deepMerge(kindOfAdmin)).withCookies(Cookie("token", t.token))
              }
            }
            case Some(sc) => {
              val t = Token()
              Token.save(t)
              SellerCompany.updateTokenAuthentification(sc, t)
              Ok(jsonConnection.deepMerge(kindOfSellerCompany)).withCookies(Cookie("token", t.token))
            }
          }
          case Some(su) => {
            val t = Token()
            Token.save(t)
            SimpleUser.updateTokenAuthentification(su, t)
            Ok(jsonConnection.deepMerge(kindOfSimpleUser)).withCookies(Cookie("token", t.token))
          }
        }
      }
    )
  }

}
