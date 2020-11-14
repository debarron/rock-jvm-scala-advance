package lectures.part4implicits


// HTMLSerializer = Type class
// Implementors of typeclass = typeclass instances
trait MyTypeClassTemplate[T]{
  def action(value:T): String
}

