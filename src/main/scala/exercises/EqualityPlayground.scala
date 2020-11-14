package exercises

import lectures.part4implicits.TypeClasses.{HTMLSerializer, User}

object EqualityPlayground extends App{

  trait HTMLWritable{
    def toHtml: String
  }

  case class User(name:String, age:Int, email:String) extends HTMLWritable{
    override def toHtml: String =
      s"<div>$name ($age yo) <a href=$email/></div>"
  }
  val jhon = User("John", 32, "jhon@some.com")
  println(jhon.toHtml)
  /*
  Exercise equal type
   */
  trait Equal[T]{
    def compare(a:T, b :T):Boolean
  }

  object UserByNameEqual extends Equal[User]{
    override def compare(a: User, b: User): Boolean =
      a.name.compareTo(b.name) == 0
  }
  object UserByNameEmailEqual extends Equal[User]{
    override def compare(a: User, b: User): Boolean =
      a.name.compareTo(b.name) == 0 && a.age == b.age
  }
  /*
   Another exercise
    - Implement the TC pattern with to Equal Typeclass
    */
  object Equal{
    def compare[T](a:T, b:T)(implicit comparable:Equal[T]):Boolean =
      comparable.compare(a, b)

    def apply[T](implicit comparable:Equal[T]) = comparable
  }
  implicit object IntComparable extends Equal[Int] {
    override def compare(a: Int, b: Int): Boolean = a == b
  }
  implicit object UserComparable extends Equal[User] {
    override def compare(a: User, b: User): Boolean =
      a.name.compare(b.name) == 0 && a.age == b.age
  }

  implicit class EqualEnrichment[T](value:T){
    def ===(another:T)(implicit comparable:Equal[T]):Boolean =
      comparable.compare(value, another)

    def !==(another:T)(implicit comparable:Equal[T]):Boolean = {
      val t = implicitly[Equal[T]]
      !comparable.compare(value, another)
    }
  }

  println(Equal.compare(jhon, jhon))
  println(Equal.compare(1, 2))
  println(2 === 3)
  println(jhon !== jhon)


}
