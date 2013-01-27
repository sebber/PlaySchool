package accounting

case class User(id: Long, username: String, password: String)

object User {
  
}


class ChangeUsernameOn(user: User) {

  lazy val userRepo = UserRepository.get

  def to(username: String) {
    
    
  }

}