package lectures.part4implicits

object TypeClasses extends App{
  trait HTMLWritable{
    def toHtml: String
  }
  case class User(name:String, age:Int, email:String) extends HTMLWritable{
    override def toHtml: String =
      s"<div>$name ($age yo) <a href=$email/></div>"
  }
  val jhon = User("John", 32, "jhon@some.com")
  println(jhon.toHtml)


  /*
  1 - for the types we write
  2- One implementation out of hundreds
   */

  // option 2
//  object HTMLSerializerPM{
//    def serializeToHtml(value:Any) = value match{
//      case User(n, a, e) =>
//      case _ =>
//    }
//  }
  /*
  1- Lost type safety
  2- Need to modiify the code every time
  3- Still one implementation
   */

  // Option 3
  trait HTMLSerializer[T]{
    def serialize(value:T) : String
  }
  implicit object UserSerializer extends HTMLSerializer[User]{
    override def serialize(value: User): String =
      s"<div>${value.name} (${value.age} yo) <a href=${value.email}/></div>"
  }
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date]{
    override def serialize(value: Date): String =
      s"<div>${value.toString()}</div>"
  }
  object PartialUserSerializer extends HTMLSerializer[User]{
    override def serialize(value: User): String = s"<div>${value.name}</div>"
  }

  // part 2
  object HTMLSerializer{
    def serialize[T](value:T)(implicit serializer:HTMLSerializer[T]):String =
      serializer.serialize(value)

    // Get a hold on the implicit object and use it
    def apply[T](implicit serializer:HTMLSerializer[T]) = serializer
  }
  implicit object IntSerializer extends HTMLSerializer[Int] {
    def serialize(v:Int):String = s"<div color=blue>$v</div>"
  }
  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(jhon))

  // part 3
  implicit class HTMLEnrichment[T](value:T){
    def toHTML(implicit serlializer:HTMLSerializer[T]):String =
      serlializer.serialize(value)
  }

  println(jhon.toHTML)
  println(2.toHTML)


  /*
  TYPE CLASS Pattern
   - Type class itself --- HTMLSerializer[T]
   - Type class instances --- UserSerializer, IntSerializer
   - Conversions with implicit class --- HTMLEnrichment
   */
}
