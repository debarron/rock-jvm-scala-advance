package lectures.part1as

object AdvencePatternMatching extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println("Only head remaining")
    case _ =>
  }

  /**
   * Pattern matching patterns
   *  - Constants
   *  - Wildcards
   *  - Case classes
   *  - Tuples
   *  - Some special magic
   */

  // Imagine no case-class available
  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 32)
  val greetings = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a years old"
  }
  println(greetings)

  // Can overload unapply
  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /** EXERCISE. Match with multiple conditions
   * */
  object even {
    def unapply(x: Int): Option[String] =
      if (x % 2 != 0) None
      else Some("even number")
  }

  object singleDigit {
    def unapply(x: Int): Option[String] =
      if (x > 9) None
      else Some("single digit")
  }

  object evenBool {
    def unapply(x: Int): Boolean = x % 2 == 0
  }

  object singleDigitBool {
    def unapply(arg: Int): Boolean = (arg < 10)
  }

  val prop = 42 match {
    case even(prop) => s"It has $prop"
    case singleDigit(prop) => s"It is $prop"
    case _ => "No property"
  }
  println(prop)

  val prop2 = 42 match {
    case evenBool() => s"It has $prop"
    case singleDigitBool() => s"It is $prop"
    case _ => "No property"
  }
  println(prop2)

  // Part 2
  case class Or[A, B](a: A, b: B) // Either in Scala
  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number written as $string"
    // case  Or(number, string) => s"$number is written as $string"
  }

  val varargs = numbers match {
    case List(1, _*) => "string containing 1"
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Const[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] = {
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
    }
  }

  val myList: MyList[Int] = Const(1, Const(2, Const(3, Empty)))
  val decompose = myList match {
    case MyList(1, 2, _*) => "Starts with 1 and 2"
  }
  println(decompose)

  // In order to use unapply, we have to implement:
  // isEmpty:Boolean
  // get:Something
  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false

      def get = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"Person's name is $n"
    case _ => "Has no name"
  })
}
