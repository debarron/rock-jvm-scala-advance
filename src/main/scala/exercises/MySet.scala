package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  /** EXECCICE
   * implement:
   * - contains => Boolean
   * - add => MySet[A]
   * - union => MySet[A]
   * - map, flatMap, filter
   */

  def apply(a:A):Boolean = contains(a)
  def contains(a:A): Boolean
  def +(a:A):MySet[A]
  def ++(other:MySet[A]):MySet[A]
  def map[B](f:A=>B):MySet[B]
  def flatMap[B](f:A=>MySet[B]):MySet[B]
  def filter(f:A=>Boolean):MySet[A]
  def forEach(f:A=>Unit):Unit

  /**
   * EXERCISE 2
   * - remove an element
   * - insersect with another set
   * - difference with another set
   */

  def -(a:A):MySet[A] //remove
  def ^(other:MySet[A]):MySet[A] // instesect
  def --(other:MySet[A]):MySet[A] // difference
  def unary_! : MySet[A]
}

class DSetEmpty[A] extends MySet[A]{
  def contains(a:A) = false
  def +(a:A) = new DSet[A](a, this)
  def ++(ms:MySet[A]):MySet[A] = ms
  def map[B](f:A=>B):MySet[B] = new DSetEmpty[B]
  def flatMap[B](f:A=>MySet[B]):MySet[B] = new DSetEmpty[B]
  def filter(f:A=>Boolean):MySet[A] = this
  def forEach(f:A=>Unit):Unit = ()

  // E #2
  def -(a:A):MySet[A] = this
  def ^(other:MySet[A]):MySet[A] = this
  def --(other:MySet[A]):MySet[A] = this

  // E #3
  def unary_! :MySet[A] = new PropertyBasedSet[A](_=>true)
}

class PropertyBasedSet[A](property:A=>Boolean) extends MySet[A]{
  def contains(a: A): Boolean = property(a)

  // {x in A| property(x)} + a == {x in A| propterty(x) || x== element}
  def +(a: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == a)

  def ++(other: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || other(x))

  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def forEach(f: A => Unit): Unit = politelyFail

  def filter(f: A => Boolean): MySet[A] =
    new PropertyBasedSet[A](x => property(x) && f(x))


  def -(a: A): MySet[A] = filter(_!=a)

  def ^(other: MySet[A]): MySet[A] = filter(other)
  def --(other: MySet[A]): MySet[A] = filter(!other)

  def unary_! : MySet[A] =
    new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep into the RH")
}


class DSet[A](head:A, tail:MySet[A]) extends MySet[A]{
  def contains(a:A) =
    head == a || tail.contains(a)

  def +(a:A):MySet[A] =
    if (this.contains(a)) this
    else new DSet[A](a, this)

  def ++(ms:MySet[A]):MySet[A] =
    tail ++ ms + head

  def map[B](f:A=>B):MySet[B] =
    (tail map f) + f(head)

  def flatMap[B](f:A=>MySet[B]):MySet[B] =
    (tail flatMap f) ++ f(head)

  def forEach(f:A=>Unit):Unit = {
    f(head)
    (tail forEach f)
  }

  def filter(f:A=>Boolean):MySet[A] = {
    val filteredTail = tail filter f
    if(f(head)) filteredTail + head
    else filteredTail
  }

  // E #2
  def -(a:A):MySet[A] =
    if(a == head) tail
    else tail - a + head

  def ^(other:MySet[A]):MySet[A] = filter(other)
  def --(other:MySet[A]):MySet[A] = filter(!other)

  def unary_! :MySet[A] = new PropertyBasedSet[A](!contains(_))
}

object MySet{
  def apply[A](a:A*):MySet[A] = {
    @tailrec
    def buildSet(valuesSeq:Seq[A], acc:MySet[A]):MySet[A] = {
      if (valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)
    }
    buildSet(a.toSeq, new DSetEmpty[A])
  }
}

object MySetPlayground extends App{
  val myset = MySet(1, 2, 3, 4)
  myset + 5 ++ MySet(11, 12, 13) +
    4 flatMap(x=>MySet(x, x*10)) map(_+1) filter(_ % 2 == 0) forEach println

  println(myset(20))

  val mySecondSet = MySet(1, 34, 5)
  myset ^ mySecondSet forEach println
  println("---")
  myset -- mySecondSet forEach println
  println("---")
  mySecondSet - 34 forEach println
}