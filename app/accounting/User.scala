package accounting

case class User(id: Long, username: String, password: String) {

  lazy val userRepo = UserRepository.get

  def change_username(new_username: String) = userRepo.change_username(id, new_username) 
}

object User {
  
}


class ChangeUsernameOn(user: User) {

  lazy val userRepo = UserRepository.get

  def to(username: String) {
    
    
  }

}