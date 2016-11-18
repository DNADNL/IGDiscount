package controllers

import javax.inject.{Inject, Singleton}

import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import models.SellerCompany

/**
  * Created by kevin on 18/11/16.
  */
@Singleton
class ManageOrderController @Inject() extends Controller {

  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  val jsonRequiredOwner = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  val jsonErrorParameter = Json.obj(
    "error" -> true,
    "message" -> "Parameter error"
  )

  val jsonOrderCreated = Json.obj(
    "error" -> false,
    "message" -> "Order created"
  )

  def createOrder(idSimpleUser : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u : SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                BasketRow.findAllBasket(u).flatMap(br =>
                  if (br.quantity > br.product.quantity) br.product.name
                  else {
                    br.product.quantity -= br.quantity
                    br.product.update()
                    br.delete()
                    Order(br.quantity, OrderState.PENDING, br.product.price, br.product, br.simpleUser).save()
                    "ok"
                  }
                )
                Created(jsonOrderCreated)
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  def getOrderSimpleUser(idSimpleUser : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u : SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                Ok(Json.toJson(Order.findAll()))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  def getOrderSellerCompany(idSellerCompany : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                Ok(Json.toJson(Order.findAll(sc)))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

}
