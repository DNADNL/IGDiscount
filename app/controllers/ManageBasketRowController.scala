package controllers

import javax.inject.{Inject, Singleton}

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by kevin on 16/11/16.
  */
@Singleton
class ManageBasketRowController @Inject() extends Controller {

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

  val jsonBasketRowCreated = Json.obj(
    "error" -> false,
    "message" -> "Added cart"
  )

  val jsonProductNotFoundBasket =Json.obj(
    "error" -> true,
    "message" -> "Product not found on the basket"
  )

  val jsonBasketRowUpdated =Json.obj(
    "error" -> true,
    "message" -> "Basket updated"
  )

  val jsonBasketRowDelete =Json.obj(
    "error" -> false,
    "message" -> "Product removed"
  )

  val jsonProductNotFound =Json.obj(
    "error" -> true,
    "message" -> "Product not found"
  )

  val parameterBasket = Form(
    "quantity" -> number(min = 0)
  )

  /**
    *
    * @param idSimpleUser
    * @return
    */
  def getBasket(idSimpleUser : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u : SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                Ok(Json.toJson(BasketRow.findAllBasket(u)))
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

  def deleteBasket(idSimpleUser : Long, idProduct : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u: SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                Product.find(idProduct) match {
                  case Some(p) => BasketRow.findByProductAndSimpleUser(p, u) match {
                    case Some(br) => {
                      br.delete()
                      Ok(jsonBasketRowDelete)
                    }
                    case _ => NotFound(jsonProductNotFoundBasket)
                  }
                  case _ => NotFound(jsonProductNotFound)
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

  def updateBasket(idSimpleUser : Long, idProduct : Long) = Action { implicit request =>
    parameterBasket.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val quantity = formData
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case u: SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    Product.find(idProduct) match {
                      case Some(p) => BasketRow.findByProductAndSimpleUser(p, u) match {
                        case Some(br) => {
                          br.quantity = quantity
                          br.update()
                          Ok(jsonBasketRowCreated)
                        }
                        case _ => NotFound(jsonProductNotFoundBasket)
                      }
                      case _ => NotFound(jsonProductNotFound)
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
    )
  }

  def createBasketRow(idSimpleUser : Long, idProduct : Long) = Action { implicit request =>
    parameterBasket.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val quantity = formData
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case u : SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    BasketRow(Product.find(idProduct).get, u, quantity).save()
                    Created(jsonBasketRowCreated)
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
    )
  }

}
