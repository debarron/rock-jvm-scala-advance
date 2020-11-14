package lectures.part5typesystem

import com.sun.org.apache.bcel.internal.generic.BranchInstruction

object StructuralTypes extends App{
  // structural types
  type JavaClosable = java.io.Closeable

  // another closable
  class HipsterClosable{
    def close():Unit = println("hipser yea yea")
    // Type refinements
    def closeSilently():Unit = println("shh")

  }

  // Implement something to accept both
//  def closeQuietly(JavaClosable | HipsterClosable ):Unit
  type UnifiedClosable = {
    def close():Unit
  } // Structral tyipe

  def closeQuietly(closable:UnifiedClosable): Unit = ???
  closeQuietly(new JavaClosable{
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterClosable)

  // Type Refienments
  type AdvanceClosable = JavaClosable {
    def closeSilently():Unit
  }

  class AdvanceJavaClosable extends JavaClosable{
    override def close(): Unit = println("Close Java")
    def closeSilently():Unit = println("Close Shh")
  }
  def closeShh(advClosable:AdvanceClosable):Unit = {
    advClosable.closeSilently()
  }
  closeShh(new AdvanceJavaClosable)
//  closeShh(new HipsterClosable) Error not related to JavaClosable

  def altClose(closable: {def close():Unit}):Unit = closable.close()

  // Duck-typing

  type SoundMaker = {
    def makeSound():Unit
  }
  class Dog{
    def makeSound():Unit = println("woof")
  }
  class Car{
    def makeSound():Unit = println("booom!")
  }
  val dog:SoundMaker = new Dog
  val car:SoundMaker = new Car

  // CAVEAT, based on reflection thus expensive

  /*
  Exercises
   */

  trait CBL[+T]{
    def head:T
    def tail:CBL[T]
  }
  class Human{
    def head:Brain = new Brain
  }
  class Brain{
    override def toString:String = "Brainz!"
  }

  // 1. Is it valid, yes it is compatible with Human and CBL
  def f[T](somethingWithAHead: {def head:T}):Unit = println(somethingWithAHead.head)
  // proof
  case object CBNil extends CBL[Nothing]{
    def head: Nothing = ???
    def tail: CBL[Nothing] = ???
  }
  case class CBCons[T](override val head:T, override val tail:CBL[T]) extends CBL[T]
  f(CBCons(2, CBNil))
  f(new Human)

  object HeadEqualizer{
    type Headable[T] = {def head:T}
    def ===[T](a:Headable[T], b:Headable[T]):Boolean = a.head == b.head
  }

  val brainList = CBCons(new Brain, CBNil)
  HeadEqualizer.===(brainList, new Human)

  // Cool, but remember it is based on reflection
  val stringList = CBCons("Hello", CBNil)
  HeadEqualizer.===(stringList, new Human)

}
