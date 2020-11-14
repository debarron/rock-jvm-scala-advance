package lectures.part5typesystem

object PathDependentTypes extends App{
  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i:Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod:Int = {
    class InnerClass
    type HelperType = String
    2
  }

  // per-instance
  val o = new Outer
  val inner = new o.Inner

  val oo = new Outer
  // val otherInner: oo.Inner = inner WRONG, different types
  o.print(inner)
//  oo.print(inner) ERROR wrong types
//  path-dependant types we have to use Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
  Exercise
  DB keyed by Int or String, but maybe others

  use path-dependant types
  abstract types or type aliases
   */

  trait ItemLike{
    type Key
  }
  trait Item[K] extends ItemLike{
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42)
  get[StringItem]("hello")
//  get[IntItem]("home")



}
