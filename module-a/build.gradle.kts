import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  scala
  java
  id("com.github.johnrengelman.shadow") version "4.0.4"
}

dependencies {
  compileOnly(project(path = ":module-b"))
  implementation("org.scala-lang:scala-library:2.11.6")
}

tasks {

  named("build") {
    dependsOn(withType<ShadowJar>())
  }

  withType<Jar> {
    manifest {
      attributes(mapOf(
        "Main-Class" to "com.module.a.ModuleA"
      ))
    }
  }

}
