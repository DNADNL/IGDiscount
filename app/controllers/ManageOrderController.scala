package controllers

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}

import models.{SellerCompany, _}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

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

  val jsonInsufficientStock = Json.obj(
    "error" -> true,
    "message" -> "Insufficient stock"
  )

  val jsonOrderUpdated = Json.obj(
    "error" -> false,
    "message" -> "Order updated"
  )

  val jsonOrderPaid = Json.obj(
    "error" -> false,
    "message" -> "Order paid"
  )

  val jsonOrderNotConfirmed = Json.obj(
    "error" -> false,
    "message" -> "Order not confirmed by seller"
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

  def getOrderSellerCompanyCancelledOrPaid(idSellerCompany : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                Ok(Json.toJson(Order.findAllCancelledOrPaid(sc)))
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

  def updateOrderConfirm(idSimpleUser : Long, idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                val o = Order.find(idOrder).get
                if (o.state == OrderState.PENDING && o.quantity <= o.product.quantity) {
                  o.state = OrderState.CONFIRMED_BY_SELLER
                  o.product.quantity -= o.quantity
                  o.product.update()
                  o.stateDate = new Timestamp(new Date().getTime)
                  o.update()
                  Ok(jsonOrderUpdated)
                }
                else {
                  Conflict(jsonInsufficientStock)
                }
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

  def getOrderConfirmed = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => {
            Ok(Json.toJson(Order.findAllConfirmed()))
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  def updateOrderPaid(idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => {
            val o = Order.find(idOrder).get
            if (o.state == OrderState.CONFIRMED_BY_SELLER) {
              o.state = OrderState.PAID
              o.stateDate = new Timestamp(new Date().getTime)
              o.update()
              Ok(jsonOrderPaid)
            }
            else {
              Conflict(jsonOrderNotConfirmed)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  def updateOrderCancel(idSimpleUser : Long, idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                val o = Order.find(idOrder).get
                if (o.state == OrderState.PENDING && o.quantity <= o.product.quantity) {
                  o.state = OrderState.CANCELLED_BY_SELLER
                  o.stateDate = new Timestamp(new Date().getTime)
                  o.update()
                  Ok(jsonOrderUpdated)
                }
                else {
                  Conflict(jsonInsufficientStock)
                }
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
