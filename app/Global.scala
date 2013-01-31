import play.api._

import accounting._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    val userRepo = UserRepository.get 
    if(userRepo.getAll.isEmpty)
    {
      List(
        User(1, "basse", "hemligt"),
        User(2, "bengt", "hemligt")
      ).foreach(userRepo.insert)
    }
  }
  
}