package lectures.part5typesystem

object SelfTypes extends App{
  // Required a type to be mixed

  trait Instrumentalist{
    def play():Unit
  }

  trait Singer{ self:Instrumentalist =>
    def sing():Unit
  }

  // In order to extend Singer we need to implement Instrumentalist
  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  val jamesHetfield = new Singer with Instrumentalist{
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist{
    override def play(): Unit = ???
  }
  val ericClapton = new Guitarist with Singer{
    override def sing(): Unit = ???
  }


  class Component{
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component:Component)

  // Cake pattern
  trait ScalaComponent{
    //API
    def action(x:Int):String
  }
  trait ScalaDependentComponent {self:ScalaComponent =>
    def dependentAction(x:Int):String = action(x) + "awesome"
  }
  trait ScalaApplication { self:ScalaDependentComponent => }

  // layer-1 small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer-2 compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer-3 app
  trait AnalyticsApp extends  ScalaApplication with Analytics

  // Cyclical dependencies
  // ERROR
  class X extends Y
  class Y extends X

  // Works!
  trait A { self:B => }
  trait B { self:A => }


}
