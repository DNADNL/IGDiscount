package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SimpleUser, Token}
import models.models.SellerCompany
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Cookie}

/**
  * Created by kevin on 31/10/16.
  */
@Singleton
class ManageUserController @Inject() extends Controller {

  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  val jsonRequiredAdmin = Json.obj(
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

  val jsonUserDeleted = Json.obj(
    "error" -> false,
    "message" -> "User deleted"
  )

  val jsonUserNoDeleted = Json.obj(
    "error" -> true,
    "message" -> "User not deleted"
  )

  val jsonUserUpdated = Json.obj(
    "error" -> false,
    "message" -> "User updated"
  )

  val jsonErrorEmail = Json.obj(
    "error" -> true,
    "message" -> "Email already exists"
  )

  val parameterSimpleUserWithoutPassword = Form(
    tuple(
      "email" -> email,
      "postalCode" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  val parameterSellerCompanyWithoutPassword = Form(
    tuple(
      "email" -> email,
      "postalCode" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> nonEmptyText,
      "siret" -> nonEmptyText,
      "companyName" -> nonEmptyText
    )
  )

  val parameterAdminWithoutPassword = Form(
    tuple(
      "email" -> email,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  val parameterChangePassword = Form(
    "password" -> nonEmptyText(minLength = 5)
  )

  def getSimpleUser(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SimpleUser.find(id)))
          case su : SimpleUser => SimpleUser.tokenConform(c.value, SimpleUser.find(id).getOrElse(new SimpleUser())) match {
            case true => Ok(Json.toJson(SimpleUser.find(id)))
            case false => Unauthorized(jsonRequiredAdmin)
          }
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def getSellerCompany(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SellerCompany.find(id)))
          case sc : SellerCompany => SellerCompany.tokenConform(c.value, SellerCompany.find(id).getOrElse(new SellerCompany())) match {
            case true => Ok(Json.toJson(SellerCompany.find(id)))
            case false => Unauthorized(jsonRequiredAdmin)
          }
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def getAdmin(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(Admin.find(id)))
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def getAllSimpleUser = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SimpleUser.findAll()))
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def getAllSellerCompany = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SellerCompany.findAll()))
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def getAllAdmin = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(Admin.findAll()))
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def deleteSimpleUser(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            SimpleUser.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def deleteSellerCompany(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            SellerCompany.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def deleteAdmin(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            Admin.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Unauthorized(jsonRequiredAdmin)
        }
        case _ => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

  def updateSimpleUserWithoutPassword(id : Long) = Action { implicit request =>
    parameterSimpleUserWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, postalCode, street, city, streetNumber, firstName, lastName) = formData
        val su = SimpleUser(id, email, null, postalCode, street, city, streetNumber, firstName, lastName)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SimpleUser.updateWithoutPassword(su) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    SimpleUser.updateWithoutPassword(su) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Unauthorized(jsonRequiredAdmin)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

  def updateSellerCompanyWithoutPassword(id : Long) = Action { implicit request =>
    parameterSellerCompanyWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, postalCode, street, city, streetNumber, siret, companyName) = formData
        val sc = SellerCompany(id, email, null, postalCode, street, city, streetNumber, siret, companyName)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SellerCompany.updateWithoutPassword(sc) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SellerCompany => {
                SellerCompany.tokenConform(c.value, u) match {
                  case true => {
                    SellerCompany.updateWithoutPassword(sc) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Unauthorized(jsonRequiredAdmin)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

  def updateAdminWithoutPassword(id : Long) = Action { implicit request =>
    parameterAdminWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, firstName, lastName) = formData
        val a = Admin(id, email, null, firstName, lastName)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case x : Admin => {
                Admin.updateWithoutPassword(a) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

  def updateSimpleUserPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val su = SimpleUser(id, password, null , null, null, null, null, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SimpleUser.updatePassword(su) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    SimpleUser.updatePassword(su) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Unauthorized(jsonRequiredAdmin)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

  def updateSellerCompanyPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val sc = SellerCompany(id, null, password, null, null, null, null, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SellerCompany.updatePassword(sc) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SellerCompany => {
                SellerCompany.tokenConform(c.value, u) match {
                  case true => {
                    SellerCompany.updatePassword(sc) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Unauthorized(jsonRequiredAdmin)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

  def updateAdminPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val a = Admin(id, null, password, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case x : Admin => {
                Admin.updatePassword(a) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case _ => Unauthorized(jsonRequiredAdmin)
            }
            case _ => Unauthorized(jsonTokenExpired)
          }
          case _ => Forbidden(jsonNoToken)
        }
      }
    )
  }

}
