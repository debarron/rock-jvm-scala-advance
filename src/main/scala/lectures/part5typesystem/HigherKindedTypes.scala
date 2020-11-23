package lectures.part5typesystem

object HigherKindedTypes extends App{
  /*
  Deeper generic types
   */

  trait AHihgherKindedType[F[_]]

  trait MyList[T]{
    def flatMap[B](f:T=>B): MyList[B]
  }
  trait MyOption[T]{
    def flatMap[B](f:T=>B): MyList[B]
  }
  trait MyFuture[T]{
    def flatMap[B](f:T=>B): MyList[B]
  }

  // Let's make it general
  trait Monad[F[_], A]{
    def flatMap[B](f: A=>F[B]):F[B]
    def map[B](f: A=>B):F[B]
  }
  class MonadList[A](list:List[A]) extends Monad[List, A]{
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }
  class MonadOption[A](option:Option[A]) extends Monad[Option, A]{
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  def multiply[F[_], A, B](ma:Monad[F, A], mb:Monad[F, B]):F[(A,B)] = for{
      a <- ma
      b <- mb
    } yield (a, b)

  println(multiply(new MonadList(List(1,2,3)), new MonadList(List("a", "b"))))
  println(multiply(new MonadOption(Some(2)), new MonadOption(Some("Scala"))))

}
