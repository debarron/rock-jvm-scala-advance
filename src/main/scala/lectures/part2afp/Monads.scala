package lectures.part2afp

object Monads extends App{

  // Implement Try
  trait Attempt[+A]{
    def flatMap[B](f:A => Attempt[B]):Attempt[B]
  }
  object Attempt{
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a)
      }catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Success[+A](value:A) extends Attempt[A]{
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try{
        f(value)
      }catch{
        case e:Throwable => Fail(e)
      }
  }
  case class Fail(e:Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  class Lazy[+A](value: =>A){
    private lazy val internalValue = value
    def use:A = internalValue
    def flatMap[B](f:(=>A)=>Lazy[B]):Lazy[B] = f(internalValue)
    def map[B](f:(=>A)=>B):Lazy[B] = flatMap(x =>Lazy(f(x)))

    //def flatten(m:Lazy[Lazy[A]]):Lazy[A] = m.flatMap(x => x)
  }
  object Lazy{
    def apply[A](a: =>A): Lazy[A] = new Lazy(a)
  }

  val lazyInstance = Lazy{
    println("Something something")
    42
  }
  val flatMappedInstance = lazyInstance.flatMap(x => Lazy{
    10 * x
  })

  println(List(List(1,2,3), List(4,5)).flatten)



}
