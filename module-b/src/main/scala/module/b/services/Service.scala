package module.b.services

import module.b.dtos.MyDTO

class Service {
  def getDto(): Option[MyDTO] = Some(new MyDTO)
}
