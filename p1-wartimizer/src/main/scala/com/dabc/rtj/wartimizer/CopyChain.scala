package com.dabc.rtj.wartimizer

import quoted.*

/**
 * Given
 * ```scala
 * case class Person(name: String, age: Int, favLanguage: String, gamer: Boolean)
 * ```
 *
 * Before:
 * ```scala
 * person
 *  .copy(name: "Someone")
 *  .copy(age = 99)
 *  .copy(favLanguage = "Java")
 * ```
 * After
 * ```scala
 * person.copy(name: "Someone", age = 99, favLanguage = "Java")
 * ```
 *
 *
 */
object CopyChain extends Wartimization {

  //case class Args (q: Quotes) extends AnyVal {
  //  import q.reflect.*
  //  def unapply(term: Term): Option[(Term, List[Term], List[List[Term]])] = {
  //
  //    None
  //  }
  //}

  object Args {
    //inline def apply(using Quotes): Args = new Args(quotes)

    def unapply[Q <: Quotes](using q: Quotes)(term: q.reflect.Term): Option[(q.reflect.Term, List[q.reflect.Term], List[List[q.reflect.Term]])] = {

      None
    }
  }

  def treeMap(using q: Quotes): q.reflect.TreeMap = {
    import quotes.reflect.*

    def isDefaultCopy(term: Term): Boolean =
      term.symbol.flags.is(Flags.Synthetic) && term.symbol.name.contains("copy$default$")

    def nonDefaultCopy(term: Term): Boolean = !isDefaultCopy(term)

    def extracLatestArgs(target: List[Term], chain: List[List[Term]]): List[Term] = {
      target.zip(chain.transpose) // List[(Term, List[Term])]
        .map { (t, cs) => cs.findLast(nonDefaultCopy).getOrElse(t) }
    }

    // TODO: move to Args; rename Args to something that reflects its role
    def getCopyMethod(target: Term): Symbol = {
      target.symbol
        .methodMember("copy")
        .headOption
        // this should NEVER happen
        .getOrElse { report.errorAndAbort(s"The impossible happened; there is no copy method on ${target.symbol.name}", target.pos) }
    }

    new TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = {
        tree match {
            // figure out the latest args to apply to the final copy method
          case Args(target, targetArgs, chainArgs) =>
            val args = extracLatestArgs(targetArgs, chainArgs)

            // find the copy method to invoke
            val copyMethod = getCopyMethod(target)

            // invoke the copy method on those args
            target.select(copyMethod).appliedToArgs(args).changeOwner(owner)

          // base case
          case _ => super.transformTerm(tree)(owner)
        }
      }
    }
  }
}
