package controllers.admin

import play.api._
import play.api.mvc._
import views.html._

object Application extends GandalfController {

  def index = withAuth { username => implicit request =>
    Ok(views.html.Admin.Application.index("Welcome to admin"))
  }

  def help = withAuth { username => implicit request =>
    Ok(views.html.Admin.Application.index("Helppage"))
  }

}