package org.wartremover
package warts

object Null extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val UnapplyName: TermName = "unapply"
    val UnapplySeqName: TermName = "unapplySeq"
    val xmlSymbols = List(
      "scala.xml.Elem", "scala.xml.NamespaceBinding"
    ) // cannot do `map rootMirror.staticClass` here because then:
      //   scala.ScalaReflectionException: object scala.xml.Elem in compiler mirror not found.

    val optionSymbol = rootMirror.staticClass("scala.Option")
    val OrNull: TermName = "orNull"

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        val synthetic = isSynthetic(u)(tree)
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          // Ignore xml literals
          case Apply(Select(left, _), _) if xmlSymbols.contains(left.tpe.typeSymbol.fullName) =>
          // Ignore synthetic methods in companion objects
          case ModuleDef(mods, _, Template(parents, self, stats)) =>
            mods.annotations foreach { annotation =>
              traverse(annotation)
            }
            parents foreach { parent =>
              traverse(parent)
            }
            traverse(self)
            stats filter {
              case dd: DefDef if isSynthetic(u)(dd) =>
                false
              case _ =>
                true
            } foreach { stat =>
              traverse(stat)
            }
          case Literal(Constant(null)) =>
            u.error(tree.pos, "null is disabled")
            super.traverse(tree)

          //var s: String = _
          case ValDef(mods, name, t, _)
            if mods.hasFlag(Flag.DEFAULTINIT) && !isPrimitive(u)(t.tpe) && !(t.tpe <:< typeOf[Unit]) =>
            u.error(tree.pos, "null is disabled")

          // Option.orNull (which returns null) is disabled.
          case Select(left, OrNull) if left.tpe.baseType(optionSymbol) != NoType =>
            u.error(tree.pos, "Option#orNull is disabled")

          // Scala pattern matching outputs synthetic null.asInstanceOf[X]
          case ValDef(mods, _, _, _) if mods.hasFlag(Flag.MUTABLE) && synthetic =>
          // TODO: This ignores a lot
          case LabelDef(_, _, _) if synthetic =>
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
