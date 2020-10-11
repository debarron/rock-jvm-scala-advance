package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {
  val aCondition: Boolean = true
  val aConditionVal = if (aCondition) 42 else 0

  val theUnit = println("Hello world")

  def aFunction(x: Int): Int = x + 1

  @tailrec def factorial(x: Int, acc: Int): Int =
    if (x <= 0) acc
    else factorial(x - 1, x * acc)

  class Animal

  class Dog extends Animal

  val fido: Animal = new Dog

  trait Carnivour {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivour {
    override def eat(a: Animal): Unit = println("Crouch!")
  }

  val aCroc = new Crocodile
  aCroc.eat(fido)
  aCroc eat fido

  val anonimousCarnivour = new Carnivour {
    override def eat(a: Animal): Unit = println("Roar!")
  }

  abstract class MyList[+A]

  object MyList

  // Serializable, toString, compare, hash
  case class Person(name: String, age: Int)

  val throws = new RuntimeException
  val aPotentialEx = try {
    throws
  } catch {
    case e: Exception => "got it"
  } finally {
    println("some logs")
  }

  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val annonimousIncrementer = (x: Int) => x + 1

  val pairs = for {
    num <- List(1, 2, 3)
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  val aMap = Map(
    "Daniel" -> 123,
    "Betania" -> 345,
    "Ana" -> 678
  )

  val anOption = Some(4)
  val x = 2
  val order = x match {
    case 1 => "First"
    case 2 => "Second"
    case _ => x + "th"
  }

  val bob = Person("Bob", 42)
  val greetings = bob match {
    case Person(name, _) => s"Hello $name"
  }

}
