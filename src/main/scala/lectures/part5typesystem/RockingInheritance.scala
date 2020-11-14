package lectures.part5typesystem

object RockingInheritance extends App{
  // Convenience
  trait Writer[T]{
    def write(value:T):Unit
  }
  trait Closable{
    def close(status:Int):Unit
  }
  trait GenericStream[T]{
    def foreach(f:T=>Unit):Unit
  }

  // stream it's is own type
  def processStream[T](stream:GenericStream[T] with Writer[T] with Closable):Unit ={
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal { def name: String }
  trait Lion extends Animal { override def name: String = "Lion"}
  trait Tiger extends Animal { override def name: String = "Tiger"}
  class Mutant extends Lion with Tiger{ }
  // Mutant.name returns Tiger, since 'with' is the last override

  // super problem == type linearization
  trait Cold{
    def print = println("Cold")
  }
  trait Green extends Cold{
    override def print: Unit = {
      println("Green")
      super.print
    }
  }
  trait Blue extends Cold{
    override def print: Unit = {
      println("Blue")
      super.print
    }
  }
  class Red{
    def print = println("Red")
  }
  class White extends Red with Green with Blue{
    override def print: Unit = {
      println("White")
      super.print
    }
  }
  /*
  Cold = AnyRef with <Cold>
  Green = AnyRef with Cold with <Green>
  Blue = AnyRef with Cold with <Blue>
  Red = AnyRef with <Red>

  -- In herarchical three we got:
  White =
  AnyRef with Red
  with (AnyRef with Cold with <Green>) **(skips AnyRef)
  with (AnyRef with Cold with <Blue>)  **(skips AnyRef with Cold)
  with <White>

  -- Type linearization skips what it saw before, final is:
  White = AnyRef with Red with Cold with Green with Blue with White

  -- 'super' calls the element on the left of the chain:
  White.print -> super (moves to the next element on the left)
              Blue.print -> super (moves ..)
                        Green.print -> super (moves ..)
                                    Cold.print

   */

  val color = new White
  color.print
}
