package exercises

import scala.annotation.tailrec

/**
 * EXERCISE Implement lazy evaluated singly linked STREAM
 * of elements
 * naturals = MyStream.from(0)(x => x+1) == Naturals number
 * naturals.take(99) // lazy eval
 * naturals.take(99).foreach(println)
 * naturals.foreach(println) // would crash
 * naturals.map(_ * 1) // stream of all numbers potentially infinite
 */
abstract class MyStream[+A]{
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element:B): MyStream[B] // prepend operator
  def ++[B >: A](another: => MyStream[B]): MyStream[B] // connect two streams

  def foreach(f:A => Unit):Unit
  def map[B](f:A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]):MyStream[B]
  def filter(f:A=>Boolean):MyStream[A]

  def take(n:Int): MyStream[A] // takes the first n-elements out of the stream
  def takeAsList(n:Int):List[A] = take(n).toList()

  @tailrec
  final def toList[B>:A](acc:List[B] = Nil):List[B] =
    if(isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object MyStream {
  def from[A](start:A)(generator: A=>A):MyStream[A] =
    new ConsStream[A](start, MyStream.from(generator(start))(generator))
}

object MyEmptyStream extends MyStream[Nothing]{
  def isEmpty:Boolean = true
  def head:Nothing = throw new NoSuchElementException
  def tail:MyStream[Nothing] = this

  def #::[B >: Nothing](element:B):MyStream[B] =
    new ConsStream[B](element, this)

  def ++[B >: Nothing](other: => MyStream[B]):MyStream[B] =
    new ConsStream[B](other.head, other.tail)

  def foreach(f:Nothing => Unit):Unit = ()
  def map[B](f:Nothing => B):MyStream[B] = this
  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(f:Nothing => Boolean):MyStream[Nothing] = this

  def take(n:Int):MyStream[Nothing] = this
}

class ConsStream[+A](hd:A, tl: =>MyStream[A]) extends MyStream[A]{
  def isEmpty:Boolean = false
  override val head:A = hd
  override lazy val tail = tl

  def #::[B>:A](element:B):MyStream[B] =
    new ConsStream[B](element, this)

  // Should maintain lazy eval
  def ++[B >:A](another: => MyStream[B]):MyStream[B] =
    new ConsStream[B](head, tail ++ another)

  def foreach(f:A=>Unit):Unit = {
    f(head)
    tail.foreach(f)
  }
  def map[B](f:A=>B):MyStream[B] =
    new ConsStream[B](f(head), tail.map(f))

  def flatMap[B](f:A=>MyStream[B]):MyStream[B] =
    f(head) ++ tail.flatMap(f)

  def filter(f:A=>Boolean):MyStream[A] =
    if (f(head)) new ConsStream(head, tail.filter(f))
    else tail.filter(f)

  def take(n:Int):MyStream[A] =
    if(n <= 0) MyEmptyStream
    else if (n == 1) new ConsStream(head, MyEmptyStream)
    else new ConsStream(head, tail.take(n-1))
}

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_+1)
  println(naturals.head)
  println(naturals.tail)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)

  startFrom0.take(100).foreach(println)

  // map & flatMap
  println(startFrom0.map(_*2).take(100).toList())
  println(startFrom0.flatMap(x => new ConsStream(x, new ConsStream(x+1, MyEmptyStream))).take(10).toList())
  println(startFrom0.filter(_<10).take(10).toList())

  /**
   * EXERCICE
   * Stream of fibonacci numbers
   * Stream of prime numbers with Eratosthenes sieve
   */

  def fibonacci(first:Int, second:Int):MyStream[Int] =
    new ConsStream(first, fibonacci(second, first+second))

  def eratosthenes_sieve(numbers:MyStream[Int]) : MyStream[Int] = {
    if(numbers.isEmpty) numbers
    else new ConsStream(numbers.head, eratosthenes_sieve(numbers.tail.filter(_%numbers.head != 0)))
  }

  println(fibonacci(1, 1).take(10).toList())
  println(eratosthenes_sieve(MyStream.from(2)(_+1)).take(5).toList())



}
