package controllers

import play.api._
import play.api.mvc._
import views.{ html }

import accounting._

object Users extends GandalfController {

  val userRepo = UserRepository.get

  def index = contextAction { implicit request =>
    Ok(html.Admin.Users.list(userRepo.getAll))
  }

  def show(id: Long) = contextAction { implicit request =>
    userRepo.getById(id).map { user =>
      Ok(html.Admin.Users.show(user))
    }.getOrElse(NotFound)
  }

}