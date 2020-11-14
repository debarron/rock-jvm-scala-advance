package lectures.part4implicits

object ImplicitsIntro extends App{
  // How this compiles
  val pair = "Daniel" -> "Rocks"
  val intPair = 1 -> 2

  case class PersonI(name:String) {
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(str:String):PersonI = PersonI(str)
  println("Peter".greet) // println(fromStringToPerson().greet)

  // This conflicts with the above greet declaration
  // makes the compiler go no-no
//  class A{
//    def greet:Int = 2
//  }
//  implicit def fromStringToA(str:String):A = new A

  def increment(x:Int)(implicit amount:Int) = x + amount
  implicit val defaultAmount = 10
  println(increment(20))

}
