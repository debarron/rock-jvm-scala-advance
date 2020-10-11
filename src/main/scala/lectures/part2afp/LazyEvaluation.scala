package lectures.part2afp

object LazyEvaluation extends App {
  // val x:Int = throw new RuntimeException // Throws error
  // lazy val DELAYS the evaluation of values
  lazy val x:Int = throw new RuntimeException

  lazy val y:Int = {
    println("Print once")
    42
  }
  println(y)

  // Example implications

  // 1. Side effects
  //  - Boo won't be printed
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  // 2. In conjuction with call by name
  def byNameMethod(n: => Int):Int = n + n + n + 1
  def retrievalMagicValue: Int  = {
    println("waiting")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod((retrievalMagicValue)))

  // In practice, we use lazy vals
  // Call by need
  def byNameMethodCBN(n: => Int):Int = {
    lazy val t:Int = n
    t + t + t + 1
  }
  println(byNameMethodCBN((retrievalMagicValue)))

  // 3. Filtering
  def lessThan30(i:Int):Boolean ={
    println(s"$i less than 30? ")
    i < 30
  }
  def greaterThan20(i:Int):Boolean = {
    println(s"$i is greater than 20")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  gt20Lazy.foreach(println)


  // 4. for-comprehensions use withFilter with guards
  for{
    a <- List(1, 2, 3) if a % 2 == 0
  } yield a +1
  // List(1, 2, 3).withFilter(_ % 2 == 0).map(_+1)


}
