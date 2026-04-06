package com.dabc.rtj
package wartimizer

import quoted.*
import macros.*

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

  extension [K, V] (kv: Map[K, V])
    def apply(key: K, default: => V): V = kv.getOrElse(key, default)

  private def resolveArguments(using Quotes)(args: List[qr.Term], vals: List[qr.Statement]): List[qr.Term] = {
    import qr.*

    // map[value name, value expression]
    val expressions: Map[String, Term] = vals.collect {
      case ValDef(name, _, Some(expression)) => name -> expression
    }.toMap

    args.map {
      case Ident(name) if expressions.contains(name) => expressions(name)
      // age = some intermediate value
      case NamedArg(argName, term @ Ident(termName)) => NamedArg(argName, expressions(termName, term))
      // anything else is left intact
      case term => term
    }
  }

  // https://docs.scala-lang.org/scala3/reference/changed-features/pattern-matching.html#extractors
  private def unapply(using Quotes)(term: qr.Term): Option[(qr.Term, List[qr.Term], List[List[qr.Term]])] = {
    import qr.*

    term match {
      // recursive: chain.copy(args)
      case Block(
        ValDef(localVal, _, Some(CopyChain(target, targetArgs, chainArgs))) // final target of the copy call; computed recursively
          :: intermediateVals, // local value definitions, maybe used in the last copy call
        Apply(Select(Ident(finalTarget), "copy"), args)
      ) if localVal == finalTarget =>
        val localArgs = resolveArguments(args, intermediateVals)
        Some((target, targetArgs, chainArgs :+ localArgs))

      // chain copy: target.copy(args).copy(args2)
      case Block(intermediateVals, Apply(Select(target, "copy"), targetArgs)) =>
        Some(target, resolveArguments(targetArgs, intermediateVals), List())

      // simple copy case: target.copy(arguments)
      case Apply(Select(target, "copy"), targetArgs) =>
        // no copy chain
        Some((target, targetArgs, List()))

      // base case
      case _ => None
    }
  }

  private def nonDefaultCopy(using Quotes)(term: qr.Term): Boolean =
    !term.symbol.flags.is(qr.Flags.Synthetic) || !term.symbol.name.contains("copy$default$")

  private def extractLatestArgs(using Quotes)(target: List[qr.Term], chain: List[List[qr.Term]]): List[qr.Term] = {
      target.zip(chain.transpose) // List[(Term, List[Term])]
        .map { (t, cs) => cs.findLast(nonDefaultCopy).getOrElse(t) }
    }

  private def getCopyMethod(using Quotes)(target: qr.Term): qr.Symbol = {
    target.symbol
      .methodMember("copy")
      .headOption
      // this should NEVER happen
      .getOrElse { qr.report.errorAndAbort(s"The impossible happened; there is no copy method on ${target.symbol.name}", target.pos) }
  }

  def treeMap(using Quotes): qr.TreeMap = new qr.TreeMap {
    override def transformTerm(tree: qr.Term)(owner: qr.Symbol): qr.Term = {
      tree match {
          // figure out the latest args to apply to the final copy method
        case CopyChain(target, targetArgs, chainArgs) =>
          val args = extractLatestArgs(targetArgs, chainArgs)

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
