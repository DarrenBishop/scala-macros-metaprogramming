package com.dabc.rtj.wartimizer

import quoted.*

object Wartimizer {
  inline def wartimize[A](inline w: Wartimization, inline ws: Wartimization*)(block: A): A =
    ${ wartimizeImpl('w, 'ws, 'block) }
      
  private def wartimizeImpl[A : Type](w: Expr[Wartimization], ws: Expr[Seq[Wartimization]], block: Expr[A])(using q: Quotes): Expr[A] = {
    import q.reflect.*
    
    ???
  }
}
