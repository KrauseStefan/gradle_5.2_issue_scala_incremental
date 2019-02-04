package com.module.a

import module.b.services.Service

object ModuleA extends App {
  new Service().getDto().foreach(dto => println(dto.message))
}
