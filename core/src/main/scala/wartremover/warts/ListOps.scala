package org.wartremover
package warts

@deprecated("Use TraversableOps.", "Wartremover 1.1.2")
object ListOps extends WartTraverser {

  class Op(name: String, error: String) extends WartTraverser {
    override lazy val className = "org.wartremover.warts.ListOps"

    def apply(u: WartUniverse): u.Traverser = {
      import u.universe._

      val symbol = rootMirror.staticClass("scala.collection.immutable.List")
      val Name: TermName = name
      new u.Traverser {
        override def traverse(tree: Tree): Unit = {
          tree match {
            // Ignore trees marked by SuppressWarnings
            case t if hasWartAnnotation(u)(t) =>
            case Select(left, Name) if left.tpe.baseType(symbol) != NoType =>
              error(u)(tree.pos, error)
            // TODO: This ignores a lot
            case LabelDef(_, _, rhs) if isSynthetic(u)(tree) =>
            case _ =>
              super.traverse(tree)
          }
        }
      }
    }
  }

  def apply(u: WartUniverse): u.Traverser =
    WartTraverser.sumList(u)(List(
      new Op("head", "List#head is disabled - use List#headOption instead"),
      new Op("tail", "List#tail is disabled - use List#drop(1) instead"),
      new Op("init", "List#init is disabled - use List#dropRight(1) instead"),
      new Op("last", "List#last is disabled - use List#lastOption instead"),
      new Op("reduce", "List#reduce is disabled - use List#reduceOption or List#fold instead"),
      new Op("reduceLeft", "List#reduceLeft is disabled - use List#reduceLeftOption or List#foldLeft instead"),
new Op("reduceRight", "List#reduceRight is disabled - use List#reduceRightOption or List#foldRight instead")
    ))

}
