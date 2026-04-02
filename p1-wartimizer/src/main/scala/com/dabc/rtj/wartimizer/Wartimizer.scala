package com.dabc.rtj.wartimizer

import quoted.*

object Wartimizer {
  inline def wartimize[A](inline w: Wartimization, inline ws: Wartimization*)(inline block: A): A =
    ${ wartimizeImpl('w, 'ws, 'block) }

  private def wartimizeImpl[A : Type](w: Expr[Wartimization], ws: Expr[Seq[Wartimization]], block: Expr[A])(using q: Quotes): Expr[A] = {
    import q.reflect.*

    // get wartimization instances
    val wartimizations = w.valueOrAbort +: ws.valueOrAbort

    // get tree-maps
    val treeMaps = wartimizations.map(_.treeMap)

    // apply all tree-maps on the tree
    val wartimized = treeMaps.foldLeft(block.asTerm) { (currTree: Tree, treeMap: TreeMap) =>
      treeMap.transformTree(currTree)(Symbol.spliceOwner)
    }

    wartimized.asExprOf[A]
  }
}
