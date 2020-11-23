package lectures.part5typesystem

object FBoundedPolumorphism extends App{
//  trait Animal{
//    def breed: List[Animal]
//  }
//  class Cat extends Animal{
//    override def breed: List[Animal] = ??? // Want list of Cats
//  }
//  class Dog extends Animal{
//    override def breed: List[Animal] = ??? // Want a list of dogs
//  }

  // Solution 1 - Naive, types are covariant
//  trait Animal{
//    def breed: List[Animal]
//  }
//  class Cat extends Animal{
//    override def breed: List[Cat] = ??? // Want list of Cats
//  }
//  class Dog extends Animal{
//    override def breed: List[Dog] = ??? // Want a list of dogs
//  }

  // Solution 2 - Recursive types, F-Bounded polymorphism
//  trait Animal[A <: Animal[A]]{
//    def breed: List[Animal[A]]
//  }
//  class Cat extends Animal[Cat]{
//    override def breed: List[Animal[Cat]] = ??? // Want list of Cats
//  }
//  class Dog extends Animal[Dog]{
//    override def breed: List[Animal[Dog]] = ??? // Want a list of dogs
//  }
//  // Problem
//  class Crocodile extends Animal[Dog]{
//    override def breed: List[Animal[Dog]] = ???
//  }

  // Solution 3 - FBP + Self type
//  trait Animal[A <: Animal[A]]{ self:A =>
//    def breed: List[Animal[A]]
//  }
//  class Cat extends Animal[Cat]{
//    override def breed: List[Cat] = ??? // Want list of Cats
//  }
//  class Dog extends Animal[Dog]{
//    override def breed: List[Dog] = ??? // Want a list of dogs
//  }
//  // Solution to the Crocodile problem
//  class Crocodile extends Animal[Dog]{
//    override def breed: List[Animal[Dog]] = ???
//  }
//  // But, can't be effective for one more level of poly
//  trait Fish extends Animal[Fish]
//  class Shark extends Fish{
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Solution 4 - Type classes
//  trait Animal
//  trait CanBreed[A] {
//    def breed(a:A): List[A]
//  }
//  class Dog extends Animal
//  object Dog{
//    implicit object DogsCanBreed extends CanBreed[Dog]{
//      override def breed(a: Dog): List[Dog] = List()
//    }
//  }
//  implicit class CanBreedOpt[A](animal:A){
//    def breed(implicit canbreed:CanBreed[A]): List[A] =
//      canbreed.breed(animal)
//  }
//  val dog = new Dog
//  dog.breed // List[Dog]


  // Soluton 5 - Pure type classes
  trait Animal[A]{
    def breed(a:A): List[A]
  }
  class Dog
  object Dog{
    implicit object DogsAnimal extends Animal[Dog]{
      override def breed(a: Dog): List[Dog] = List()
    }
  }
  implicit class AnimalEnrichments[A](animal:A){
    def breed(implicit animalInstance:Animal[A]): List[A] =
      animalInstance.breed(animal)
  }
  val dog = new Dog
  dog.breed // List[Dog]

}
