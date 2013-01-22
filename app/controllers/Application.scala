package controllers

import play.api._
import play.api.mvc._

import views.{ html }


object Application extends GandalfController {

  def index = contextAction { implicit context =>
    Ok(html.Admin.Application.index("Welcome to admin"))
  }

  def help = contextAction { implicit context =>
    Ok(html.Admin.Application.index("Helppage"))
  }

}