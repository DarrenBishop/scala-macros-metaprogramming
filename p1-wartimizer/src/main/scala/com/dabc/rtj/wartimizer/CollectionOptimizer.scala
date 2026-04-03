package com.dabc.rtj.wartimizer

import quoted.Quotes

object CollectionOptimizer extends Wartimization {

  def treeMap(using q: Quotes): q.reflect.TreeMap = {
    import q.reflect.*

    new TreeMap {
      override def transformTree(tree: Tree)(owner: Symbol): Tree = {
        val maybeExpr = Option.when(tree.isExpr)(tree).map(_.asExpr)
        val maybeTransformedExpr = maybeExpr.collect {
          // TODO: add more cases
          case '{($x: collection.Map[k, v]).get($key).getOrElse($value) } =>
            report.info("Use `.getOrElse(...)` instead", x.asTerm.pos)
            '{ $x.getOrElse($key, $value) }

          case '{($x: collection.Iterable[t1]).map[t2]($f).map[t3]($g) } =>
            report.info("Use function composition instead", x.asTerm.pos)
            '{ $x.map(a => $g($f(a))) }

          case '{($x: collection.Iterable[t]).filter($f).headOption } =>
            report.info("Use `.find(...)` instead", x.asTerm.pos)
            '{ $x.find($f) }

          case '{($x: collection.Iterable[t]).filter($f).size } =>
            report.info("Use `.count(...)` instead", x.asTerm.pos)
            '{ $x.count($f) }

          case '{($x: collection.Iterable[t]).collect($f).headOption } =>
            report.info("Use `.collectFirst(...)` instead", x.asTerm.pos)
            '{ $x.collectFirst($f) }

          case '{($x: collection.Iterable[t1]).map[Unit]($f) } =>
            report.info("Use `.foreach(...) instead", x.asTerm.pos)
            //'{ $x.foreach(a => $f(a)) }
            report.errorAndAbort("Use `.foreach(...)` instead", tree.pos)
        }

        maybeTransformedExpr
          .map(_.asTerm) // Option of transformed term
          .map(term => transformTerm(term)(owner)) // continue transforming until there's nothing left
          .getOrElse(super.transformTree(tree)(owner))
      }
    }
  }
}
