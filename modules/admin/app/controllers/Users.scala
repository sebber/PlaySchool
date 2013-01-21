package controllers.admin

import play.api._
import play.api.mvc._
import views.html._

import accounting._

object Users extends Controller with Secured {

  val userRepo = UserRepository.get

  def index = withAuth { username => implicit request =>
    Ok(views.html.Admin.Users.list(userRepo.getAll))
  }

  def show(id: Long) = withAuth { username => implicit request =>
    userRepo.getById(id).map { user =>
      Ok(views.html.Admin.Users.show(user))
    }.getOrElse(NotFound)
  }

}