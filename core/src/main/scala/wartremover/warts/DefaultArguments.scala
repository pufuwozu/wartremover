package org.wartremover
package warts

object DefaultArguments extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._
    import u.universe.Flag._

    def containsDef(v : List[ValDef]) =
      v.find(_.mods.hasFlag(DEFAULTPARAM)).isDefined

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case d@DefDef(_, _, _, vs, _, _) if !isSynthetic(u)(d) && vs.find(containsDef).isDefined =>
            error(u)(tree.pos, "Function has default arguments")
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
