package lectures.part4implicits

import java.util.Date

object JsonSerialization extends App{
  /*
  Users, posts, feeds
  Serialize to JSON
   */

  case class User(name:String, age:Int, email:String)
  case class Post(content:String, createdAt:Date)
  case class Feed(user:User, posts:List[Post])

  /*
  1. Create intermediate data types (Int, String, List, Date
  2. Type classes for conversions to intermediate data types
  3. Serialize to JSON
   */

  sealed trait JSONValue{ // Intermediate data type
    def stringify:String
  }
  final case class JSONString(value:String) extends JSONValue{
    override def stringify: String = "\"" + value + "\""
  }
  final case class JSONNumber(value:Int) extends  JSONValue{
    override def stringify: String = value.toString
  }
  final case class JSONArray(values:List[JSONValue]) extends JSONValue{
    override def stringify: String =
      values.map(_.stringify).mkString("[", ",", "]")
  }
  final case class JSONObject(values:Map[String, JSONValue]) extends  JSONValue{
    override def stringify: String = values.map{
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("ScalaRocks!"),
      JSONNumber(4345)
    ))
  ))
  println(data.stringify)

  /*
   Type classes
   1. Type class
   2. Type class instances
   3. Pimp library
   */
  //1
  trait JSONConverter[T]{
    def convert(value:T):JSONValue
  }

  //2 Conversion
  implicit class JSONOps[T](value:T){
    def toJSON(implicit converter:JSONConverter[T]):JSONValue =
      converter.convert(value)
  }

  //2 Existing data types
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value:String):JSONValue = JSONString(value)
  }
  implicit object NumberConverter extends JSONConverter[Int]{
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // 2 Custom data types
  implicit object UserConverter extends JSONConverter[User]{
    override def convert(user: User): JSONValue =
      JSONObject(Map(
        "name" -> JSONString(user.name),
        "age" -> JSONNumber(user.age),
        "email" -> JSONString(user.email)
      ))
  }
  implicit object PostConverter extends JSONConverter[Post]{
    override def convert(value: Post): JSONValue =
      JSONObject(Map(
        "content" -> JSONString(value.content),
        "createdAt" -> JSONString(value.createdAt.toString)
      ))
  }
  implicit object FeedConverter extends JSONConverter[Feed]{
    override def convert(value: Feed): JSONValue =
      JSONObject(Map(
        "user" -> value.user.toJSON, //TODO
        "posts" -> JSONArray(value.posts.map(_.toJSON)) //TODO
      ))
  }

  // Call stringify on the result
  val date  = new Date(System.currentTimeMillis())
  val dan = User("Daniel", 30, "d@d.com")
  val feed = Feed(dan, List(
   Post("Hello", date),
   Post("Look at this", date)
  ))

  println(feed.toJSON.stringify)
}
