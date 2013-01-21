package controllers

import play.api._
import play.api.mvc._

case class ContextedRequest(
  context: Context, private val request: Request[AnyContent]
) extends WrappedRequest(request)

class Context(request: RequestHeader) { 

  def isAuthenticated = request.session.get(Security.username).isDefined

  def isAnonymous = !isAuthenticated

  def getUsername = request.session.get(Security.username)

}

trait GandalfController extends Controller with Secured {

  def contextAction(f: ContextedRequest => Result) = {
    Action { request =>
      f(ContextedRequest(new Context(request), request))
    }    
  }

}