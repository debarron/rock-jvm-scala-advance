package lectures.part5typesystem

object Variance extends App{
  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type sustitution of generics

  // covariance   => declared generic, accepts specifics
  // invariance   => declared specific, accept specific
  // contravariance => declared specific, accept generic

  // Covariance example
  class CCage[+T]
  val ccage:CCage[Animal] = new CCage[Cat]

  // Invariance
  class ICage[T]
  val icage:ICage[Cat] = new ICage[Cat]
//  val err:ICage[Animal] = new ICage[Cat]

  // Contravariance
  class XCage[-T]
  val xcage:XCage[Cat] = new XCage[Animal]
  val xcage2:XCage[Animal] = new XCage[Animal]


  class InvariantCage[T](val animal:T) // invariant
  // covariant positions

//  class CovariantCage[+T](var animal:T) ERROR
//  would generate: x:CC[Animal] = CC[Cat]; x.animal = new Dog

//  class ContravariantCage[-T](val animal:T) ERROR
//  would generate: CC[Cat] = new CC[Dog]

  // To deal with this, we make sure that types are related
  class MyList[+T]{
    // B is super-type of T
    def add[B >: T](element:B):MyList[B] = new MyList[B]
  }
  /*
  Big Rule:
  - Method arguments are in contravariant position
  - Return types are in covariant position
   */

  /**
   * 1.
   * Invariant, covariant and contravariant parking of things
   * Parking[T](things:List[T])
   *  - park - parks a vehicle
   *  - impound - vehicles: List[T]
   *  - checkVehicles(conditions:String) List[T]
   *
   *  2. Use IList instead of List
   *
   *  3. Make Parking a monad
   *   - flatMap
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle


  /**
   * 1
   */
  class IPark[T](vehicles:List[T]){
    def park(vehicle:T):IPark[T] = ???
    def impound(vs:List[T]):IPark[T] = ???
    def checkVehicles(conditions:String):List[T] = ???

    def flatMap[S](f:T=>IPark[S]):IPark[S] = ???
  }
  val generalPark:IPark[Vehicle] = new IPark[Vehicle](List(new Vehicle))
  generalPark.park(new Bike)
  generalPark.park(new Car)

  class CPark[+T](vehicles:List[T]){
    def park[B >: T](v:B):CPark[B] = ???
    def impound[B >: T](vehicles:List[B]): CPark[B] = ???
    def checkVehicles(conditions:String):List[T] = ???

    def flatMap[S](f:T=>CPark2[S]):CPark2[S] = ???
  }

  class XPark[-T](vehicles:List[T]){
    def park(v:T):XPark[T] = ???
    def impound(vehicles:List[T]):XPark[T] = ???
    def checkVehicles[B <:T](condition:String):List[B] = ???

    def flatMap[R <: T, S](f:R=>XPark2[S]):XPark2[S]
  }

  /*
  Rule of thumb
  - Use covariance for collection of things
  - use contravariance for group of actions
   */


  /**
   * 2
   */
  class IList[T]
  class CPark2[+T](vehicles:IList[T]){
    def park[B >: T](v:B):CPark[B] = ???
    def impound[B >: T](vehicles:IList[B]): CPark[B] = ???
    def checkVehicles[B >: T](conditions:String):IList[B] = ???
  }

  class XPark2[-T](vehicles:IList[T]){
    def park(v:T):XPark[T] = ???
    def impound[B <: T](vehicles:IList[B]):XPark[T] = ???
    def checkVehicles[B <:T](condition:String):IList[B] = ???
  }

  /**
   * 3
   */
}
