package module.b.services

import module.b.moved.dtos.MyDTO

class Service {
  def getDto(): Option[MyDTO] = Some(new MyDTO)
}
