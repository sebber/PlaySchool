package accounting

trait UserRepository {

  def getAll(): Seq[User]

  def getById(id: Long): Option[User]

  def getByUsername(username: String): Option[User]

}


object UserRepository {

  def get(): UserRepository = new SimpleUserRepository with UserRepository

}

class SimpleUserRepository {

  val users: Seq[User] = List(
    User(1, "basse", "hemligt"),
    User(1, "bengt", "hemligt")
  )

  def getAll = users

  def getById(id: Long): Option[User] = {
    users.find((user: User) => user.id == id)
  }

  def getByUsername(username: String): Option[User] = {
    users.find((user: User) => user.username == username)
  }
}