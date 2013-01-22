package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

sealed abstract class Context(val request: RequestHeader) { 

  def isAuthenticated = request.session.get(Security.username).isDefined

  def isAnonymous = !isAuthenticated

  def getUsername = request.session.get(Security.username)

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


  protected def contextAction(f: Context => Result): Action[AnyContent] =
    contextAction(BodyParsers.parse.anyContent)(f)
  protected def contextAction[A](p: BodyParser[A])(f: Context => Result): Action[A] =
    Action(p)(request => f(requestToContext(request)))

  
  protected def contextActionPost(f: BodyContext => Result): Action[AnyContent] =
    contextActionPost(BodyParsers.parse.anyContent)(f)
  protected def contextActionPost[A](p: BodyParser[A])(f: BodyContext => Result): Action[A] =
    Action(p)(request => f(requestToContext(request)))


  protected def requestToContext(req: Request[_]): BodyContext = Context(req)

  protected def requestToContext(req: RequestHeader): HeaderContext = Context(req)

}