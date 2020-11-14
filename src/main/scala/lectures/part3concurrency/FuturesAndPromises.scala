package lectures.part3concurrency

import com.sun.media.sound.SoftCubicResampler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Random, Success, Try}

object FuturesAndPromises extends App{

  def calculateMeaningOfLife:Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  }

  println(aFuture.value) // Option[Try[Int]]
  aFuture.onComplete(t => t match{
    case Success(value) => println(s"The meaning of life is $value")
    case Failure(e) => println(s"I have failed with $e")
  }) // Some thread will run this thread, we won't make assumptions

  Thread.sleep(3000)

  // Mini social network
  case class Profile(id:String, name:String){
    def poke(anotherProfile:Profile):Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }
  object SocialNetwork{
    val names = Map(
      "fb.id.1-zuk" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuk" -> "fb.id.2-bill"
    )
    val random = new Random()
    def fetchProfile(id:String):Future[Profile] = Future{
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }
    def fetchBestFriend(profile:Profile): Future[Profile] = Future{
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: Mark to poke Bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuk")
  mark.onComplete{
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete{
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(exception) => exception.printStackTrace()
  }

  // Functional composition
  // map flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val bestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val bestFriendRestricted = bestFriend.filter(profile => profile.name.startsWith("z"))

  // for comprehensions
  for{
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuk")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // Fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown").recover{
    case e: Throwable => Profile("fb.id.0-zummy", "Dummy")
  }
  val anotherProfile = SocialNetwork.fetchProfile("unknown").recoverWith{
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }
  val yetAnother = SocialNetwork.fetchProfile("Unknow").fallbackTo(
    SocialNetwork.fetchProfile("fb.id.0-dummy")
  )

  case class User(name:String)
  case class Transaction(sender:String, reciever:String, amount:Double, status:String)
  object BankingApp{
    val name = "Bank RockTheJVM"
    def fetchUser(name:String):Future[User] = Future{
      Thread.sleep(500)
      User(name)
    }
    def createTransaction(user:User, merchant:String, amount:Double)
    :Future[Transaction] = Future{
      Thread.sleep(1000)
      Transaction(user.name, merchant, amount, "SUCCESS")
    }
    def purchase(userName:String, item:String, merchant:String, cost:Double):String ={
      // Fetch user
      // Create transaction
      // Wait for the result
      val transactionStatusFuture = for {
        user <- fetchUser(userName)
        transaction <- createTransaction(user, merchant, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }
  println(BankingApp.purchase("Daniel", "Monitor", "RTJVMStore", 3000))

  // Promises
  val promise = Promise[Int]() // "controller" over a future
  val aFutureValue = promise.future

  // Thread-1 Consumer, how to handle future completion
  aFutureValue.onComplete{
    case Success(r) => println(s"[consumer] recieved $r")
  }

  // Thread-2 Producer
  val producer = new Thread(() => {
    println("[prducer] crunching numbers")
    Thread.sleep(1000)
    // promise fullfilment
    promise.success(42)
    println("[producer] Im done")
  })
  Thread.sleep(1000)
  producer.start()

  /*
  1. Fulfill a future immediately with a value
  2. inSequence (fa, fb)
  3. first(fa, fb) whoever finishes first
  4. last(fa, fb) whoever finishes last
  5. returnUntil(action:() => Future[t], condition:T => boolean)Future[T]
  * */

  // 1. Fulfill a future immediately with value
  def fulfillImmediately[T](value:T):Future[T] = Future(value)


  // 2. In sequence
  def inSequence[A,B](fa:Future[A], fb:Future[B]):Future[B] =
    fa.flatMap(_ => fb)

  // 3. First
  def first[A](fa:Future[A], fb:Future[A]):Future[A] ={
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
//    def tryComplete(promise2:Promise[A], result:Try[A]) = result match {
//      case Failure(e) => try{ promise.failure(e) }
//      case Success(x) => try{ promise.success(x) }
//    }
////    fa.onComplete(tryComplete(promise, _))
//    fb.onComplete(tryComplete(promise, _))
  }

  // 4. Last
  def last[A](fa:Future[A], fb:Future[A]): Future[A] = {
    val promiseInitial = Promise[A]
    val promiseLast = Promise[A]
    val checkComplete = (result:Try[A]) =>
      if(!promiseInitial.tryComplete(result))
        promiseLast.complete(result)

    fa.onComplete(checkComplete)
    fb.onComplete(checkComplete)

    promiseLast.future
  }

  // 5.
  def retryUntil[A](action: () => Future[A], condition: A => Boolean):Future[A] =
    action()
      .filter(condition)
      .recoverWith{
        case _ => retryUntil(action, condition)
      }


}
