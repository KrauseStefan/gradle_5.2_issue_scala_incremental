<!--- Provide a brief summary of the issue in the title above -->

Incremental Scala Compilation can fail when moving a class in a separate module

I created a simplyfied setup with with two scala modules (**A** and **B**), where module **A** depends on module **B** with a compileOnly dependency.

Module **B** has two classes a Service class and a DTO class.
Module **A** has a single class that only imports the service not the DTO from module **B**

Module **A** does use DTO, but the type is not applied directly since it is used in a `foreach` construct

``` scala
import module.b.services.Service

object ModuleA extends App {
  new Service().getDto().foreach(dto => println(dto.message))
}
```

The service in Module **B** is defined as
``` scala
// import module.b.moved.dtos.MyDTO
import module.b.dtos.MyDTO

class Service {
  def getDto(): Option[MyDTO] = Some(new MyDTO)
}
```

The problem happens when the DTO is moved, if you see the commented out line above.
Gradle correctly rebuilds Module **B** but module **A** is not rebuild and running it fails with the following error:
``` 
Exception in thread "main" java.lang.NoClassDefFoundError: module/b/dtos/MyDTO
        at com.module.a.ModuleA$$anonfun$1.apply(ModuleA.scala:6)
        at scala.Option.foreach(Option.scala:257)
        at com.module.a.ModuleA$.delayedEndpoint$com$module$a$ModuleA$1(ModuleA.scala:6)
        at com.module.a.ModuleA$delayedInit$body.apply(ModuleA.scala:5)
        at scala.Function0$class.apply$mcV$sp(Function0.scala:40)
        at scala.runtime.AbstractFunction0.apply$mcV$sp(AbstractFunction0.scala:12)
        at scala.App$$anonfun$main$1.apply(App.scala:76)
        at scala.App$$anonfun$main$1.apply(App.scala:76)
        at scala.collection.immutable.List.foreach(List.scala:381)
        at scala.collection.generic.TraversableForwarder$class.foreach(TraversableForwarder.scala:35)
        at scala.App$class.main(App.scala:76)
        at com.module.a.ModuleA$.main(ModuleA.scala:5)
        at com.module.a.ModuleA.main(ModuleA.scala)
Caused by: java.lang.ClassNotFoundException: module.b.dtos.MyDTO
        at java.net.URLClassLoader.findClass(Unknown Source)
        at java.lang.ClassLoader.loadClass(Unknown Source)
        at sun.misc.Launcher$AppClassLoader.loadClass(Unknown Source)
        at java.lang.ClassLoader.loadClass(Unknown Source)
        ... 13 more
```

Checking dependencies with jdeps I get the following output for one class file
``` 
jdeps.exe '.\module-a\build\classes\scala\main\com\module\a\ModuleA$$anonfun$1.class'
ModuleA$$anonfun$1.class -> not found
ModuleA$$anonfun$1.class -> C:\Program Files\Java\jdk1.8.0_171\jre\lib\rt.jar
   com.module.a (ModuleA$$anonfun$1.class)
      -> java.lang
      -> module.b.dtos                       not found
      -> scala                               not found
      -> scala.runtime                       not found
```

The problem was originally experienced in a big JAVA EE application on Wildfly.
My reproduction can be found here: https://github.com/KrauseStefan/gradle_5.2_issue_scala_incremental

I have two branches in this repository, switching between the branches moved the DTO class.
The below commands are all run from the root of the repository in Powershell.

Running building and running fails if a clean build succeed on the other branch
`.\gradlew.bat build; .\run.cmd`

A clean build always succeeds 
`.\gradlew.bat clean; .\gradlew.bat build; .\run.cmd`

