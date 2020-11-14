package lectures.part4implicits

import java.util.Optional

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern extends App{

  // Method to solve overloading
  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor{
    def receive(statusCode:Int):Int
    def receive(request:P2PRequest):Int
    def receive(response:P2PResponse):Int
    def receive[T:Serializer](message:T):Int
    def receive[T:Serializer](message:T, statusCode:Int):Int
    def receive(future:Future[P2PRequest]):Int
    // lots of overloads
  }
  /* problems:
  1. Type ensurance
  2. Lifting doesn't work for all overloads
  3. Code duplications
  4. Type inferance and default values

   */

  trait MessageMagnet[Result]{
    def apply():Result
  }

  def receive[R](mgnt:MessageMagnet[R]):R = mgnt()

  implicit class FromP2PRequest(request:P2PRequest) extends MessageMagnet[Int]{
    override def apply(): Int = {
      // logic for handling p2prequest
      println("Handling 2p2 request")
      42
    }
  }
  implicit class FromP2PResponse(response:P2PResponse) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("From P2P Response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // no more type erasure
  implicit class FromFutureP2PResponse(future:Future[P2PResponse])
    extends MessageMagnet[Int]{
    override def apply(): Int = 4
  }
  implicit class FromFutureP2PRequest(future:Future[P2PRequest])
    extends MessageMagnet[Int]{
    override def apply(): Int = 2
  }

  receive(Future(new P2PRequest))
  receive(Future(new P2PResponse))


  // Lifting functions
  trait MathLib{
    def add1(x:Int):Int = x+1
    def add1(s:String):Int = s.toInt + 1
  }

  trait AddMagnet{
    def apply():Int
  }
  def add1(magnet:AddMagnet):Int = magnet()

  implicit class AddInt(x:Int) extends AddMagnet{
    override def apply(): Int = x + 1
  }
  implicit class AddString(s:String) extends AddMagnet{
    override def apply(): Int = s.toInt + 1
  }
  val addFV = add1 _
  println(addFV(3))
  println(addFV("4"))

  /*
  Drawbacks
  1- Verbose
  2- Hard to Read
  3- Can't name or place a default value
  4- Call by name doesn't work correctly
   */

  class ToScala[T](value: =>T){
    def asScala:T = value
  }
  implicit def asScalaOptional[T](o: java.util.Optional[T]):ToScala[Option[T]] =
    new ToScala[Option[T]](if(o.isPresent) Some(o.get) else None)

  class ToJava[T](value: =>T){
    def asJava:T = value
  }
  implicit def asJavaOptional[T](o:Option[T]):ToJava[Optional[T]] =
    new ToJava[Optional[T]](o match{
      case Some(x) => Optional.of(x)
      case None => Optional.empty()
    })
}
