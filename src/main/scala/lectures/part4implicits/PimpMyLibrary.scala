package lectures.part4implicits

object PimpMyLibrary extends App{

  // Implicit classes
  implicit class RichInt(value:Int) {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(f: () => Unit): Unit = {
      def timesAuxilary(n: Int): Unit =
        if (n == 0) ()
        else {
          f()
          timesAuxilary(n - 1)
        }

      timesAuxilary(value)
    }

    def *[A](list: List[A]): List[A] = {
      def concatenation(n: Int): List[A] =
        if (n <= 0) List()
        else concatenation(n - 1) ++ list

      concatenation(value)
    }
  }


  42.isEven // Sweet! Type-enrichment aka pimping

  /*
  Exercise
   - Enrich the string class
    - asInt parse string to Int
    - encrypt
    Keep enriching int class
    - times (function)
    - * => 3 * List(1,2) == List(1,2,1,2,1,2)
   */

  implicit class RichString(value:String){
    def asInt:Int = value.toInt
    def encrypt(cypherDistance:Int):String =
      value.map(c =>(c.toInt + cypherDistance).asInstanceOf[Char])
  }

  println("43".asInt + 1)
  println("John".encrypt(2))
  println(3.times(() => println("Hello")))
  println(2 * List(1,2))

  // "6" / 2
//  implicit def stringToInt(str:String):Int = Integer.valueOf(str)
}
