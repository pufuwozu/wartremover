package org.wartremover
package test

import org.scalatest.FunSuite

import org.wartremover.warts.Null

class NullTest extends FunSuite {
  test("can't use `null`") {
    val result = WartTestTraverser(Null) {
      println(null)
    }
    assertResult(List("null is disabled"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can't use null in patterns") {
    val result = WartTestTraverser(Null) {
      val (a, b) = (1, null)
      println(a)
    }
    assertResult(List("null is disabled"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can't use null in default arguments") {
    val result = WartTestTraverser(Null) {
      class ClassWithArgs(val foo: String = null)
      case class CaseClassWithArgs(val foo: String = null)
    }
    assertResult(List("null is disabled", "null is disabled"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can't use null inside of Map#partition") {
    val result = WartTestTraverser(Null) {
      Map(1 -> "one", 2 -> "two").partition { case (k, v) => null.asInstanceOf[Boolean] }
    }
    assertResult(List("null is disabled"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can't use `Option#orNull`") {
    val result = WartTestTraverser(Null) {
      println(None.orNull)
    }
    assertResult(List("Option#orNull is disabled"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("Null wart obeys SuppressWarnings") {
    val result = WartTestTraverser(Null) {
      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      val foo = {
        println(null)
        println(None.orNull)
        val (a, b) = (1, null)
        println(a)
        Map(1 -> "one", 2 -> "two").partition { case (k, v) => null.asInstanceOf[Boolean] }
      }
    }
    assertResult(List.empty, "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("Null wart obeys SuppressWarnings in classes with default arguments") {
    val result = WartTestTraverser(Null) {
      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      class ClassWithArgs(val foo: String = null)
      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      case class CaseClassWithArgs(val foo: String = null)
    }
    assertResult(List.empty, "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
}
