package com.dabc.rtj.wartimizer
package usage

import Wartimizer.wartimize

object CollectionOptimizerUsage {

  val numbers = List(1, 2, 3, 4, 5)

  val firstEven = numbers.filter(_ % 2 == 0).headOption

  val firstEven_v2 = wartimize(CollectionOptimizer) {
    // rewritten to list.find(_ % 2 == 0)
    numbers.filter(_ % 2 == 0).headOption
  }

  // does not compile because of StringPlusAny
  //val combinedWartimized = wartimize(StringPlusAny, CollectionOptimizer) {
  //  numbers
  //    .filter(_ % 2 == 0)
  //    .headOption
  //    .map( "Scala " + _)
  //}

  val simpleMap = Map("alice" -> 123, "Bob" -> 456, "Charlie" -> 789)

  val personNumber = simpleMap.get("Martin").getOrElse(999)

  val personNumber_v2 = wartimize(CollectionOptimizer)(simpleMap.get("Martin").getOrElse(999))

  // successive map transformations
  val mappedList = numbers.map(_ + 1).map(_ * 3).map(_.toString + "Scala is great")
  val optimizedList = wartimize(CollectionOptimizer) {
    numbers.map(_ + 1).map(_ * 3).map(_.toString + "Scala is great")
  }

  val combinedCalls = numbers.map(_ + 1).map(_ * 3).map(_.toString + "Scala is great").filter(_.length > 5).headOption
  val optimizedCombinedCalls = wartimize(CollectionOptimizer) {
    numbers
      .map(_.toString + "Scala is great")
      .map(_ + 1)
      .map(_ * 3)
      .filter(_.length > 5)
      .headOption
  }

  // this should not compile
  //wartimize(CollectionOptimizer)(numbers.map(println))
}
