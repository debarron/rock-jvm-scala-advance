package lectures.part2afp

object CurriesPAF extends App {
  // Curried functions
  val superAdder:Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3)
  println(add3(5))

  // Curried method
  def curriedAdder(x:Int)(y:Int):Int = x+y
  val add4 = curriedAdder(4) _
  val explicitAdd4:Int => Int = curriedAdder(4)
  // ETA-Expansion

  println(add4(4))

  // functions != methods
  def inc(x:Int) = x+1
  List(1,2,3).map(inc) // ETA-Expansions map(x => inc(x))

  // EXERCICES
  val simpleAddFunction = (x:Int, y:Int) => x+y
  def simpleAddMethod(x:Int, y:Int) = x+y
  def curriedAddMethod(x:Int)(y:Int) = x+y

  // add7: Int => Int = y => y + 7
  // get as many different implementations
  val add7SAF1 = (x:Int) => simpleAddFunction(x, 7)
  val add7SAF2 = simpleAddFunction(7, _)

  def add7SAM1(x:Int) = simpleAddMethod(7, x)
  def add7SAM2 = simpleAddMethod(7, _)

  def add7CAM1(x:Int) = curriedAdder(7)(x)
  def add7CAM2 = curriedAdder(7) _
  val add7_u = simpleAddMethod(7, _:Int)
  val add7_c = simpleAddFunction.curried(7)

  // Underscores are powerful
  def concat(a:String, b:String, c:String) = a + b + c
  val insertName = concat("Hello my name is ", _:String, ", how are you?")
  println(insertName("Daniel"))

  val fillInTheBlanks = concat("Hello ", _:String, _:String)
  println(fillInTheBlanks("Daniel", " Scala is super awesome"))

  /** EXERCISE
   * 1. Process list of numbers, return string representation with diff formats
   *    Use: %4.2f, %8.2f, %14.12f
   *
   * 2. Difference between:
   *  - functions vs methods
   *  - parameters: by-name vs. 0-lambda
   */
  // E #1
  val integer2String = (format:String, num:Double) => format.format(num)
  val format1 = integer2String("%4.2f", _:Double)
  val format2 = integer2String("%8.2f", _:Double)
  val format3 = integer2String("%14.12f", _:Double)

  val myList = List(1.0, 2.0, 3.0)
  myList map format1 foreach println
  myList map format2 foreach println
  myList map format3 foreach println

  // E #2
  def byName(n: => Int) = n + 1
  def byFunction(f: ()=>Int) = f() + 1

  def method:Int = 42
  def parenMethod():Int = 42

  byName(4)
  byName(method)
  byName(parenMethod())
  byName(parenMethod) // ok but beware => byName(parenMethod())
//  byName(()=>4) // won't work, needs a value, not a function
//  byName(parenMethod _) // not fine with PAF

//  byFunction(42)
//  byFunction(method) // gets evaluated no ETA
//  byFunction(method())     // gets evaluated no ETA
  byFunction(()=>42)
  byFunction(parenMethod)
  byFunction(parenMethod _)


}
