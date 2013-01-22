package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

sealed abstract class Context(val request: RequestHeader) { 

  lazy val isAuthenticated = request.session.get(Security.username).isDefined

  lazy val isAnonymous = !isAuthenticated

  lazy val getUsername = request.session.get(Security.username).getOrElse("")

}

final class BodyContext(val body: Request[_]) extends Context(body)
final class HeaderContext(req: RequestHeader) extends Context(req)

object Context {

  def apply(req: RequestHeader): HeaderContext =
    new HeaderContext(req)

  def apply(req: Request[_]): BodyContext =
    new BodyContext(req)

}

trait GandalfController extends Controller with Secured {

  override def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  protected def ContextAction(f: Context => Result): Action[AnyContent] =
    ContextAction(BodyParsers.parse.anyContent)(f)
  protected def ContextAction[A](p: BodyParser[A])(f: Context => Result): Action[A] =
    Action(p)(request => f(requestToContext(request)))

  
  protected def ContextActionPost(f: BodyContext => Result): Action[AnyContent] =
    ContextActionPost(BodyParsers.parse.anyContent)(f)
  protected def ContextActionPost[A](p: BodyParser[A])(f: BodyContext => Result): Action[A] =
    Action(p)(request => f(requestToContext(request)))


  protected def AuthAction(f: Context => Result): Action[AnyContent] =
    AuthAction(BodyParsers.parse.anyContent)(f)
  protected def AuthAction[A](p: BodyParser[A])(f: Context => Result): Action[A] =
    Action(p)( req => {
      val ctx = requestToContext(req)
      if(ctx.isAuthenticated)
        f(ctx)
      else
        onUnauthorized(ctx.request)
    })

  
  protected def AuthActionPost(f: BodyContext => Result): Action[AnyContent] =
    AuthActionPost(BodyParsers.parse.anyContent)(f)
  protected def AuthActionPost[A](p: BodyParser[A])(f: BodyContext => Result): Action[A] =
    Action(p)( req => {
      val ctx = requestToContext(req)
      if(ctx.isAuthenticated)
        f(ctx)
      else
        onUnauthorized(ctx.request)
    })



  protected def requestToContext(req: Request[_]): BodyContext = Context(req)

  protected def requestToContext(req: RequestHeader): HeaderContext = Context(req)

}