package controllers

import java.io.FileOutputStream
import java.nio.file.Files
import javax.inject.{Inject, Singleton}

import akka.util.ByteString
import models._
import models.SellerCompany
import play.api.data.Form
import play.api.data.Forms._
import play.api.http.HttpEntity
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, ResponseHeader, Result}
import play.mvc.Result

/**
  * Created by kevin on 05/11/16.
  */
@Singleton
class ManageProductController @Inject() extends Controller {

  val parameterCreateProduct = Form(
    tuple(
      "description" -> nonEmptyText(minLength = 1, maxLength = 1000),
      "name" -> nonEmptyText(minLength = 1, maxLength = 40),
      "price" -> bigDecimal(precision=6,scale=2),
      "quantity" -> number(min=0, max=10000)
    )
  )

  val parameterId = Form(
    "id" -> number
  )

  val jsonErrorForm = Json.obj(
    "error" -> true,
    "message" -> "Parameters error"
  )

  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  val jsonPermission = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  val imageUpdated = Json.obj(
    "error" -> false,
    "message" -> "Image updated"
  )

  val jsonProductCreated = Json.obj(
    "error" -> false,
    "message" -> "Product created"
  )

  val jsonProductDeleted = Json.obj(
    "error" -> false,
    "message" -> "Product deleted"
  )

  val jsonRequiredAdmin = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  val jsonImageCreated = Json.obj(
    "error" -> true,
    "id" -> "id"
  )

  val jsonProductNotFound = Json.obj(
    "error" -> true,
    "messafe" -> "Product not found"
  )

  val jsonProductUpdated = Json.obj(
    "error" -> false,
    "message" -> "Product updated"
  )

  val imageMime = List("image/jpg", "image/png", "image/jpeg")

  def allProducts = Action{implicit request =>
    Ok(Json.toJson(Product.findAll()))
  }

  def allImages = Action{implicit request =>
    Ok(Json.toJson(Image.findAll()))
  }

  def image(id: Long) = Action{implicit request =>
    Product.find(id) match {
      case Some(p) => Ok(Json.toJson(p.image))
      case _ => NotFound(jsonProductNotFound)
    }
  }

  def product(id: Long) = Action{implicit request =>
    Product.find(id) match {
      case Some(p) => Ok(Json.toJson(p))
      case _ => NotFound(jsonProductNotFound)
    }
  }

  def deleteProduct(id: Long) = Action {implicit request =>
    Product.find(id) match {
      case None => NotFound(jsonProductNotFound)
      case Some(p) => {
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case sc: SellerCompany => {
                SellerCompany.tokenConform(c.value, sc) match {
                  case false => Unauthorized(jsonRequiredAdmin)
                  case true => {
                    p.quantity = -1
                    p.update()
                    Ok(jsonProductDeleted)
                  }
                }
              }
              case a : Admin => {
                p.delete()
                Ok(jsonProductDeleted)
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    }
  }

  def updateImage(id: Long) = Action {implicit request =>
    Product.find(id) match {
      case None => NotFound(jsonProductNotFound)
      case Some(p) => {
        val i = Image.findByProduct(p).get
        request.body.asMultipartFormData match {
          case Some(mf) => mf.file("image") match {
            case Some(file) => {
              (imageMime.contains(file.contentType.get) && (file.ref.file.length().toFloat/(1024*1024).toFloat) <= 5) match {
                case true => {
                  i.name = file.filename
                  i.content = Files.readAllBytes(file.ref.file.toPath)
                  i.mime = file.contentType.get
                  p.image = i
                }
                case false => BadRequest(jsonErrorForm)
              }
            }
            case _ => BadRequest(jsonErrorForm)
          }
          case _ => BadRequest(jsonErrorForm)
        }
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case u: SellerCompany =>
                SellerCompany.tokenConform(c.value, u) match {
                  case false => Unauthorized(jsonRequiredAdmin)
                  case true => {
                    i.update()
                    p.update()
                    Created(imageUpdated)
                  }
                }
              case a : Admin => {
                i.update()
                p.update()
                Created(imageUpdated)
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    }
  }

  def updateProduct(id : Long) = Action {implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case false => Unauthorized(jsonRequiredAdmin)
              case true => {
                parameterCreateProduct.bindFromRequest.fold(
                  formWithErrors => BadRequest(jsonErrorForm),
                  formData => {
                    val (description, name, price, quantity) = formData
                    Product.find(id) match {
                      case None => NotFound(jsonProductNotFound)
                      case Some(p) => {
                        p.name = name
                        p.description = description
                        p.price = price.toFloat
                        p.quantity = quantity
                        Product.update(p)
                        Ok(jsonProductUpdated)
                      }
                    }
                  }
                )
              }
            }
          }
          case a : Admin => {
            parameterCreateProduct.bindFromRequest.fold(
              formWithErrors => BadRequest(jsonErrorForm),
              formData => {
                val (description, name, price, quantity) = formData
                Product.find(id) match {
                  case None => NotFound(jsonProductNotFound)
                  case Some(p) => {
                    p.name = name
                    p.description = description
                    p.price = price.toFloat
                    p.quantity = quantity
                    Product.update(p)
                    Ok(jsonProductUpdated)
                  }
                }
              }
            )
          }
          case _ => Unauthorized(jsonPermission)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def createProduct = Action {implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            parameterCreateProduct.bindFromRequest.fold(
              formWithErrors => BadRequest(jsonErrorForm),
              formData => {
                val (description, name, price, quantity) = formData
                val p = Product(description, name, price.toFloat, quantity, sc)
                p.save()
                request.body.asMultipartFormData match {
                  case Some(mf) => mf.file("image") match {
                    case Some(file) => {
                      (imageMime.contains(file.contentType.get) && (file.ref.file.length().toFloat/(1024*1024).toFloat) <= 5) match {
                        case true => {
                          val i = Image(file.filename, file.contentType.get, file.ref.file, p)
                          i.save()
                          p.image = i
                          p.update()
                          Created(jsonProductCreated)
                        }
                        case false => {
                          p.delete()
                          BadRequest(jsonErrorForm)
                        }
                      }
                    }
                    case _ => BadRequest(jsonErrorForm)
                  }
                  case _ => BadRequest(jsonErrorForm)
                }
              }
            )
          }
          case _ => Unauthorized(jsonPermission)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }
}
