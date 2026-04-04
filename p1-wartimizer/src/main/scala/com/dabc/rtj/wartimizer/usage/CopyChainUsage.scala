package com.dabc.rtj.wartimizer
package usage

import Wartimizer.wartimize

object CopyChainUsage {

  case class Person(name: String, age: Int, favLanguage: String, gamer: Boolean)

  val daniel = Person("Daniel", 99, "Scala", true)
  val danielCopy = daniel
    .copy(name= "danielciocirlan")
    .copy(age = 102)
    .copy(favLanguage = "Scala 3", gamer = true)

  /*
    {
      val $2$ = {
        val $1$ = daniel.copy(
          name = "danielciocirlan",
          daniel.copy$default$2,
          daniel.copy$default$3,
          daniel.copy$default$4
        )
        $1$.copy(
          $1$.copy$default$1,
          age = 102,
          $1$.copy$default$3,
          $1$.copy$default$4
        )
      }
      $2$.copy(
        $2$.copy$default$1,
        $2$.copy$default$2,
        favLanguage = "Scala 3",
        gamer = true
      )
    }

  - target = original object of the copy-chain
  - target arguments:
      List(
          name = "danielciocirlan",
          daniel.copy$default$2,
          daniel.copy$default$3,
          daniel.copy$default$4
        )
  - chain arguments:
    List(
      List(
          $1$.copy$default$1,
          age = 102,
          $1$.copy$default$3,
          $1$.copy$default$4
        ),
      List(
        $2$.copy$default$1,
        $2$.copy$default$2,
        favLanguage = "Scala 3",
        gamer = true
      )
    )
  */

  //val danielCopy_v2 = wartimize(CopyChain) {
  //  daniel
  //    .copy(name = "danielciocirlan")
  //    .copy(age = 102)
  //    .copy(favLanguage = "Scala 3", gamer = true)
  //}

  val danielCopy_v3 = wartimize(CopyChain) {
    val d2 = daniel
      .copy(name = "danielciocirlan")
      .copy(age = 102)
      .copy(favLanguage = "Kotlin", gamer = true)
    d2.copy(favLanguage = "Scala 3")
  }
}
