package lectures.part5typesystem

object Reflection extends App{
  // Reflection + macros + quasiquotes

  // Reflection part
  case class Person(name:String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - Import the Scala Reflection package
  import scala.reflect.runtime.{universe => ru}

  // 1 - Mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)
  // 2 - Class object
  val clazz = m.staticClass("lectures.part5typesystem.Reflection.Person")
  // 3 - Create reflected mirror
  val cm = m.reflectClass(clazz)
  // 4 - Get the constuctor
  val constructor = clazz.primaryConstructor.asMethod
  // 5 - Reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)
  val instance = constructorMirror.apply("Daniel")
  println(instance)

  val betania = Person("Betania")
  val methodName = "sayMyName"
  val reflected = m.reflect(betania)
  // Get method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // Get the mirror
  val method = reflected.reflectMethod(methodSymbol)
  method()

  // Type erasure - Java compiler removes the generics from the code
  // but we can keep the info as meta-data using some structures
  import ru._

  val ttag = typeTag[Person]
  println(ttag.tpe)

  class MyMap[K, V]
  def getTypeArguments[T](value:T)(implicit typeTag:TypeTag[T]) = value match{
    case TypeRef(_, _, typeArgs) => typeArgs
    case _ => List()
  }
  val myMap = new MyMap[Int, String]
  println(getTypeArguments(myMap))

  def isSubtype[A, B](implicit ttagA:TypeTag[A], ttagB:TypeTag[B]):Boolean =
    ttagA.tpe <:< ttagB.tpe

  class Animal
  class Dog extends Animal
  println(isSubtype[Dog, Animal])
}
