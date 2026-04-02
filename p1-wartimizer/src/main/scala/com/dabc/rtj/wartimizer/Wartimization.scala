package com.dabc.rtj.wartimizer

import quoted.{Expr, FromExpr, Quotes}
import reflect.NameTransformer

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
 * 1. Write the Wartimizer impls => compile the instances
 * 2. `Wartimizer.wartimize(w1, w2, w3, ...)(myCode)`
 *    - `wartimizeImpl` will fetch the Wartimization instances by their NAME from the classpath
 *    - call their `treeMap` functions
 *    - the treeMaps will run on `'myCode`
 *    - will return a new expression
 *  3. Continue compiling 
 *    
 */
trait Wartimization { self: Singleton => // all Wartimization instances must be objects, so that they can be referred to as constants i.e. at compile-time
  def treeMap(using q: Quotes): q.reflect.TreeMap
}

object Wartimization {
  given FromExpr[Wartimization] with {
    /*
    `unapply` will fetch the Wartimization object from the classpath by its name
    => will use RUNTIME reflection (!)  
     */
    def unapply(w: Expr[Wartimization])(using q: Quotes): Option[Wartimization] = {
      import q.reflect.*
      given pos: Position = w.asTerm.pos

      // get the name of the Wartimization instance
      val typeSymbol = w.asTerm.tpe.typeSymbol
      
      // must check that this Wartimization is an object
      if (typeSymbol.flags.is(Flags.Module)) {
        val fullName = typeSymbol.fullName // fully qualified class name of the object
        Some(unsafeLoadObject(fullName))
      } else {
        report.errorAndAbort(s"The expression `${w.show}: ${typeSymbol.name}` does not correspond to a compile-time constant object", pos)
      }
    }
    
    private def unsafeLoadObject[A](name: String)(using q: Quotes)(using pos: q.reflect.Position): A = {
      import q.reflect.*
      
      try {
        val clazz = Class.forName(name)

        // objects are represented in the JVM as a static field called `MODULE$`
        val module = clazz.getField(NameTransformer.MODULE_INSTANCE_NAME)

        // this Field is normally fetched from a class instance, here we don't have one
        val objectInstance = module.get(null) // the object is static

        // final value
        objectInstance.asInstanceOf[A]
      }
      catch {
        case e: Throwable =>
          report.errorAndAbort(
            s"""
               |Failed to load class [$name]
               |Make sure that:
               |  - it is a top-level object (or nested in another object)
               |  - it is defined in a file separate from where this macro is being invoked
               |  - it is being referred to directly as the object rather than an alias or a val
               |""".stripMargin,
            pos)
      }
    }
  }
}
