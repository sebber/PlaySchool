package controllers

import play.api._
import play.api.mvc._
import views.{ html }

import play.api.data._
import play.api.data.Forms._

import accounting._

object Users extends BaseController {

  val userForm = Form(
    mapping(
      "id" -> longNumber,
      "username" -> text(minLength = 4),

      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying("The Passwords does not match", passwords => passwords._1 == passwords._2)

    )
    {
      (id, username, passwords) => User(id, username, passwords._1)
    }
    {
      user => Some(user.id, user.username, (user.password, ""))
    }.verifying(
      "This username is not available", user => userRepo.getAll.find( (u: User) => u.username == user.username).isEmpty
    )
  )

  val usernameForm = Form(
    single(
      "username" -> text(minLength = 4)
    ).verifying("This username is not available", username => !userRepo.getByUsername(username).isDefined)
  )

  val passwordForm = Form(
    tuple(
      "password" -> text(minLength = 6),
      "confirm" -> text
    ).verifying("The Passwords does not match", passwords => passwords match {
        case (password, confirm) => password == confirm
      }
    )
  )


  lazy val userRepo = UserRepository.get

  def index = AuthAction { implicit ctx =>
    Ok(html.Admin.Users.list(userRepo.getAll))
  }

  def show(id: Long) = AuthAction { implicit ctx =>
    userRepo.getById(id).map { user =>
      Ok(html.Admin.Users.show(user))
    }.getOrElse(NotFound)
  }

  def edit(id: Long) = AuthAction { implicit ctx =>
    userRepo.getById(id).map { user =>
      Ok(html.Admin.Users.edit(id, userForm.fill(user)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = AuthActionPost { implicit ctx =>
    implicit val request = ctx.body
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.Admin.Users.edit(id, formWithErrors)),
      user => {
        userRepo.update(id, user)
        Redirect(routes.Users.index).flashing("success" -> "User has been updated")
      }
    )
  }

  def change_username(id: Long) = AuthActionPost { implicit ctx =>
    implicit val request = ctx.body
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.Admin.Users.edit(id, formWithErrors)),
      username => {
        Ok("")
      }
    )
  }

  def change_password(id: Long) = AuthActionPost { implicit ctx =>
    implicit val request = ctx.body
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.Admin.Users.edit(id, formWithErrors)),
      username => {
        Ok("")
      }
    )
  }

} 