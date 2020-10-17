package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App{
  /** JVM threads
   * Thread = Instance of a class
   *  - Runnable interface with void run()
  */

//  val thread = new Thread(new Runnable{
//    def run():Unit = println("Running in parallel")
//  })
//  thread.start()
//  thread.join() // Waits for the thread to finish
//
//  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
//  val threadBye = new Thread(() => (1 to 5).foreach(_ => println("bye")))
//  threadHello.start()
//  threadBye.start()
//
//  // Executors
//  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => (1 to 5).foreach(_ => println("Some from pool")))
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Done for one sec")
//  })
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Almost done")
//    Thread.sleep(1000)
//    println("Done for two sec")
//  })
//  pool.shutdown() // pool won't accept anymore actions
  // pool.shutdownNow() interrupts running and sleeping threads

  /** CONCURRENCY PROBLEMS
   *
   */
    // Race condition
  def runInParallel:Unit ={
    var x = 0

    val t1 = new Thread(() => {
      x = 1
    })
    val t2 = new Thread(() => {
      x = 2
    })
    t1.start()
    t2.start()
    println(x)
  }

  //for (_ <- 1 to 100) runInParallel


  class BankAccount(var amount: Int){
    override def toString: String = "" + amount
  }
  def buy(account:BankAccount, thing:String, price:Int):Unit = {
    account.amount -= price
//    println("I've bought " + thing)
//    println("My account is now " + account)
  }

//  for(_ <- 1 to 1000000){
//    val account = new BankAccount(50000)
//    val th1 = new Thread(() => buy(account, "shoes", 3000))
//    val th2 = new Thread(() => buy(account, "iphone2", 4000))
//
//    th1.start()
//    th2.start()
//    Thread.sleep(10)
//    if(account.amount != 43000) println("AHA " + account.amount)
//  }

  // Alternatives to avoid race conditions:
  // Opt #1 Synchronized
  def buySafe(account:BankAccount, thing:String, price:Int):Unit = {
    account.synchronized {
      account.amount -= price
      println("Got myself " + thing)
      println("My account is now " + account.amount)
    }
  }

  // Opt #2 @volatile
  @volatile
  def buyVolatile(account:BankAccount, thing:String, price:Int):Unit = {
    account.amount -= price
  }

  /** EXERCICES
   * 1) Create 50 inception threads
   *  Thread1 -> Thread2 -> Thread3
   *  println('hello from threadX')
   *  in REVERSE ORDER
   *
   * 2) x = 0
   *  1 to 100 map Thread x += 1
   *  start all
   *  + What is the biggest value?
   *  + What is the smallest value?
   *
   * 3) Sleep fallacy
   *  message = ""
   *  Thread sleep 1sec print
   *  message = "!@#"
   *  print something else
   *  sleep 2sec
   *  println message
   *  + What's the value of message?
   *  + Is it guaranted?
   *  + Why? Why not?
   *
   * */

    // E1
  def solve_exec_1(message_org:String, threads:Int):Unit = {
    def exercice_helper(message: String, thread: Int, prev: Thread): Unit = {
      if (thread <= threads+1)
        exercice_helper(message_org + thread, thread + 1, new Thread(() => {
          println(message)
        }))

      prev.start()
      prev.join()
    }
    exercice_helper(message_org + 0, 0, new Thread(() => println("Hi")))
  }
  //solve_exec_1("Hello from ", 50)

  // solution
  def inceptionThreads(maxThreads:Int, i:Int = 1):Thread = new Thread(() => {
    if( i < maxThreads){
      val newThread = inceptionThreads(maxThreads, i+1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $i")
  })
//  inceptionThreads(50).start()

  // E2
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x+=1))
  threads.foreach(_.start())
  println(x)
  // biggest?? 100
  // lowest?? 1

  // E3
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(2000)
  println(message)
  /* value of message?? Scala is awesome, must of the times
  // Is it guaranted?? No
  // why? why not?
  (main thread)
    message = "Scala sucks"
    awesomeThread.start()
    sleep(2secs) --> relieves execution
  (awesomeThread)
    sleep(1sec) --> relieves execution
  (OS gives CPU to important thread for more than 2 secs)
  (OS has to decide between main and awesomeThread)
  (OS gives execution to main thread)
    println("Scala sucks")
  (awesomeThread)
    message = "Scala is awesome"
  // how to fixit?? awesomeThread.join() after main sleep
   */
}
