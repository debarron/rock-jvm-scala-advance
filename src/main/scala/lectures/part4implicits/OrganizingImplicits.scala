package lectures.part4implicits

object OrganizingImplicits extends App{

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  // Error, cause we have multiple implicits
  println(List(1,2,3,0).sorted)
  /*
  Implicits (used as implicits parameters):
   - var/val
   - object
   - accesor methods = defs with no parenthesis
  * */

  // Execercise
  case class Person(name:String, age:Int)
  object Person {
    // Implement ordering by Alphabetic name
    implicit val nameOrdering: Ordering[Person] =
      Ordering.fromLessThan((c1, c2) =>
        c1.name.compareTo(c2.name) < 0
      )
  }
  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )
  println(persons.sorted)

  /*
  * Implicit Scope
  *  - normal scope = LOCAL SCOPE
  *  - Imported scope
  *  - Companion objects of all types
  * */

  /*
  Another example is to put all the orderings in a package, and then
  import the package when necesary
  */
  object AgeOrdering{
    implicit val ordering:Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.age < b.age)
  }
  object AlphabeticOrdering{
    implicit val ordering:Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }
  import AgeOrdering._
  println(persons.sorted)

  /* Exercise
  - totalPrice = most used 50%
  - by unit count = 25%
  - by unit price = 25%
   */
  case class Purchase(nUnits:Int, unitPrice:Double)
  object Purchase{
    implicit val ordering:Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => (a.nUnits*a.unitPrice)<(b.nUnits*b.unitPrice))
  }
  object PurchaseByCount{
    implicit val ordering:Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => a.nUnits < b.nUnits)
  }
  object PurchaseByPrice{
    implicit val ordering:Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => a.unitPrice<b.unitPrice)
  }
}
