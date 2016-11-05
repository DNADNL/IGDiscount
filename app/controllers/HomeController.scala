package controllers

import javax.inject._

import models.models.SellerCompany
import models.{Admin, SimpleUser}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    //val s = SimpleUser("","","","","","","","")
    //s.save()
    Ok(views.html.index("Your new application is ready."))
    //OK(Json.toJson(SimpleUser.getAll()))
  }

  val signInForm = Form(tuple(
    "email" -> email,
    "password" -> nonEmptyText,
    "postalCode" -> nonEmptyText,
    "street" -> nonEmptyText,
    "city" -> nonEmptyText,
    "streetNumber" -> nonEmptyText,
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText
  ))

  def createUser = Action { implicit request =>
    /*signInForm.bindFromRequest.fold(
      formWithErrors => Ok("ta mere"),
      formData => {
        //val (email, password, postalCode, street, city, streetNumber, firstName, lastName) = formData
        //val s = SimpleUser(email, password, postalCode, street, city, streetNumber, firstName, lastName)
        val s = SimpleUser("","","","","","","","")
        SimpleUser.save(s)
        Ok(Json.toJson(s))
      }
    )*/
    val r = new scala.util.Random(30)
    for( a <- 1 to 10){
      val a = Admin(r.nextString(10) + "@"+ r.nextString(5) +".com", r.nextString(5),r.nextString(5),r.nextString(5))
      Admin.save(a)
      val sc = SellerCompany.apply(r.nextString(10) + "@"+ r.nextString(5) +".com", r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5))
      SellerCompany.save(sc)
      val su = SimpleUser.apply(r.nextString(10) + "@"+ r.nextString(5) +".com", r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5),r.nextString(5))
      SimpleUser.save(su)
    }
    val a = Admin("admin@gmail.com", "admin", "jean", "miche")
    Admin.save(a)
    val sc = SellerCompany.apply("seller@gmail.com", "seller", "11110", "Avenue des pigeons", "Lyon", "22", "11111111", "VendTout")
    SellerCompany.save(sc)
    val su = SimpleUser.apply("user@gmail.com", "user", "11110", "Avenue des pigeons", "Lyon", "22", "Jean", "Bon")
    SimpleUser.save(su)
    Ok(Json.toJson(Admin.findAll()))
   // Ok(Json.toJson(SimpleUser.find(0)))
    /*val email = request.body.asFormUrlEncoded.get("email")(0)
    val password = request.body.asFormUrlEncoded.get("password")(0)
    val postalCode = request.body.asFormUrlEncoded.get("postalCode")(0)
    val street = request.body.asFormUrlEncoded.get("street")(0)
    val city = request.body.asFormUrlEncoded.get("city")(0)
    val streetNumber = request.body.asFormUrlEncoded.get("streetNumber")(0)
    val firstName = request.body.asFormUrlEncoded.get("firstName")(0)
    val lastName = request.body.asFormUrlEncoded.get("lastName")(0)*/
    //val (email, password, postalCode, street, city, streeNumber, firstName, lastName) = signInForm.bindFromRequest.get
    //println(email)
    //val su = SimpleUser(email, password, postalCode, street, city, streetNumber, firstName, lastName)

  }

  def test = Action { implicit request =>
    Ok(Json.toJson(Admin.findAll()))
  }

}
