package controllers

import play.api._
import play.api.mvc._
import views.{ html }

import play.api.data._
import play.api.data.Forms._

import accounting._

object Auth extends BaseController {

  lazy val userRep = UserRepository.get

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String): Boolean = {
    userRep.getByUsername(username).map { user =>
      user.password == password
    }.getOrElse(false)
  }

  def login = ContextAction { implicit ctx =>
    Ok(html.Admin.login(loginForm))
  }

  def authenticate = ContextActionPost { implicit ctx =>
    implicit val request = ctx.body
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.Admin.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession(Security.username -> user._1)
    )
  }

  def logout = ContextAction { implicit ctx =>
    Redirect(routes.Application.index).withNewSession
  }

}

trait Secured {
  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    val userRep = UserRepository.get
    userRep.getByUsername(username).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }

}