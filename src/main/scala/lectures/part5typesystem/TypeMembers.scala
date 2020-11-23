package lectures.part5typesystem

object TypeMembers extends App{
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection{
    type AnimalType // Abstract Type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
  val cat: ac.AnimalC = new Cat

  trait MyList{
    type T
    def add (element:T):MyList
  }

  class NonEmptyList(value:Int) extends MyList{
    override type T = Int

    override def add(element: Int): MyList = ???
  }

  type CatsType = cat.type
  //val newCat: CatsType = new CatsType

  /*
  Exercice - Enforce a type to be applicable to SOME TYPES ONLY
   */
  trait MList{
    type A
    def head:A
    def tail:MList
  }

  // Extend MList for Int but not for String
  class CustomList(hd:String, tl:CustomList) extends MList{
    override type A = String
    def head = hd
    def tail = tl
  }
  class IntList(hd:Int, tl:IntList) extends MList{
    override type A = Int
    override def head: Int = hd
    override def tail: MList = tl
  }

  class TheOnlyList[T <: Number](hd:T, tl:TheOnlyList[T]) extends MList{
    override type A = T
    def head = hd
    def tail = tl
  }

  // But we can also restrict it using by a trait
  trait ApplicableToNumbersOnly{
    type A <: Number
  }
  // Throws error at compile time
//  class NumberList(hd:String, tl:NumberList) extends MList with ApplicableToNumbersOnly{
//    override type A = String
//    override def head: A = hd
//    override def tail: MList = tl
//  }


}
