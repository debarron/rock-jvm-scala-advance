package lectures.part2afp

object PartialFunctions extends App{
  val aFunction = (x:Int) => x + 1

  val aFuzzyFunc = (x:Int) =>
    if(x == 1) 42
    else if(x == 4) 34
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val nicerFuzzyFunc = (x:Int) => x match{
    case 1 => 42
    case 4 => 34
  }

  val aPartialFunc: PartialFunction[Int, Int] = {
    case 1 => 42
    case 4 => 34
  }

  println(aPartialFunc(1))
  // Throws error println(aPartialFunc(5))

  println(aPartialFunc.isDefinedAt(34))

  val liftedPF = aPartialFunc.lift
  println(liftedPF(1))
  println(liftedPF(5)) // Throws None, guard agains error

  val chainedPF = aPartialFunc.orElse[Int, Int] {
    case 5 => 123
  }
  println(chainedPF(5))

  // PF extend Normal Functions
  val aTotalFunc:Int => Int = {
    case 1 => 999
  }
  // HOFs accept PF
  val mappedList = List(1,2,3).map{
    case 1 => 12
    case 2 => 23
    case 3 => 45
  }
  println(mappedList)

  /**
   * Exercice
   * 1. Constuct PF instance with anonymous class
   * 2. Dumb chatbot as PF
   */

  //1
  val manualFussyFunc = new PartialFunction[Int, Int]{
    override def apply(a: Int): Int = a match{
      case 1 => 23
      case 2 => 34
      case 5 => 99
    }
    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x== 5
  }

  // 2
  val myChatBot :PartialFunction[String, String] = {
    case "hello" => "Hi, how are you doing?"
    case "fine" => "Good to read that"
  }

  /*1
  scala.io.Source.stdin
    .getLines()
    .foreach(line => {
      val answer = if (myChatBot.isDefinedAt(line))
        myChatBot(line)
      else "I don't know"
      println(answer)
    })
  */
  val liftedChatBot = myChatBot.lift
  scala.io.Source.stdin
    .getLines()
    .map(liftedChatBot)
    .map {
      case Some(s) => s
      case None => "no idea"
    }
    .foreach(println)


}
