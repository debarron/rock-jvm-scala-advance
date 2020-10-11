package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1 methods with single params
  def singleArgMethod(arg: Int): String = s"$arg little ducks"

  val description = singleArgMethod {
    // Something more complex
    42
  }

  // examples of #1
  val aTryInstance = Try {
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }


  // syntax sugar #2 single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }
  val aFunkyInstance: Action = (x: Int) => x + 1

  // examples of #2
  val aThred = new Thread(new Runnable {
    override def run(): Unit = println("Hello there from Scala")
  })

  val aFunkyRunnable: Thread = new Thread(() => println("Funky"))

  abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("Sweets")


  // syntax sugar #3 :: and #:: (right associative if it ends ':')
  val prependedList = 2 :: List(1, 2)
  // => List(1,2).::(2)
  // last char decides method's associativity
  2 :: 3 :: 4 :: List(1)
  List(1).::(2).::(3).::(4)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]


  // syntax sugar #4 multiword method naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is sweet"


  // syntax sugar #5 infix types
  class Composite[A, B]

  val composite: Composite[Int, String] = ???
  val funkyComposite: Int Composite String = ???

  class -->[A, B]

  val towards: Int --> String = ???


  // syntax sugar #6 update method, much like apply
  val anArray = Array(1, 2, 3)
  anArray(2) = 7

  // anArray.update(2, 7) used in mutable collections


  // syntax sugar #7 setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0

    def member = internalMember

    def member_=(value: Int): Unit = internalMember = value
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42
  // aMutableContainer.member_=(42)


}
