package accounting

import play.api.Play.current

trait UserRepository {

  def getAll(): Seq[User]

  def getById(id: Long): Option[User]

  def getByUsername(username: String): Option[User]

  def update(id: Long, user: User): Unit

  def change_username(id: Long, username: String): Unit

  def insert(user: User): Unit

  def insert(id: Long, username: String, password: String): Unit

}


object UserRepository {

  def get(): UserRepository = new AnormUserRepository with UserRepository

}

class SimpleUserRepository {

  var users: Seq[User] = List(
    User(1, "basse", "hemligt"),
    User(2, "bengt", "hemligt")
  )

  def getAll = users

  def getById(id: Long): Option[User] = users.find((user: User) => user.id == id)

  def getByUsername(username: String): Option[User] = users.find((user: User) => user.username == username)

  def update(id: Long, user: User): Unit = {}

  def insert(user: User): Unit = {
    users ++= List(user)
  }

  def insert(id: Long, username: String, password: String): Unit = {
    users ++= List(User(id, username, password))
  }
  
}

class AnormUserRepository {

  import anorm._ 
  import anorm.SqlParser._
  import play.api.db.DB


  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("username") ~ 
    get[String]("password") map {
      case id~username~password => User(id.get, username, password)
    }
  }


  def getAll = {
    DB.withConnection { implicit c =>
      SQL("select * from users")().map(row =>
        User(row[Long]("id"), row[String]("username"), row[String]("password"))
      ).toList
    } 
  }

  def getById(id: Long): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("select * from users where id = {id}").on('id -> id).as(simple.singleOpt)
    } 
  }

  def getByUsername(username: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("select * from users where username = {username}").on('username -> username).as(simple.singleOpt)
    } 
  }

  def update(id: Long, user: User): Unit = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update users
          set username = {username}, password = {password}
          where id = {id} 
        """
      ).on(
        'id -> id,
        'username -> user.username,
        'password -> user.password
      ).executeUpdate()
    }
  }

  def change_username(id: Long, username: String): Unit = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update users
          set username = {username}
          where id = {id} 
        """
      ).on(
        'id -> id,
        'username -> username
      ).executeUpdate()
    }
  }

  def insert(user: User): Unit = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        insert into users(username, password) values(
          {username}, {password}
        )
        """
      ).on(
        'username -> user.username,
        'password -> user.password
      ).executeUpdate()
    } 
  }

  def insert(id: Long, username: String, password: String): Unit = {
    
  }


}