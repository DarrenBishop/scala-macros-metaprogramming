package com.dabc.rtj.wartimizer

import scala.quoted.Quotes

/**
 * Code processor that will transform code for ONE use-case
 *
 *Example (code optimization)  
  *Before:
  *```scala
  *List(1, 2, 3).filter(_ % 2 == 0).headOption
  *```
  *After
  *```scala
  *List(1, 2, 3).find(_ % 2 == 0)
  *```
 *
 *Example (wart remover)  
  *Before:
  *```scala
  *"Scala is " + Person("Martin Odersky", "martin@epfl.com")
  *```
  *After:
  * Should not compile
  *
 */
trait Wartimization {
  def treeMap(using q: Quotes): q.reflect.TreeMap
}
