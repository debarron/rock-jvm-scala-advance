package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App{
  /** JVM threads
   * Thread = Instance of a class
   *  - Runnable interface with void run()
  */

  val thread = new Thread(new Runnable{
    def run():Unit = println("Running in parallel")
  })
  thread.start()
  thread.join() // Waits for the thread to finish

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadBye = new Thread(() => (1 to 5).foreach(_ => println("bye")))
  threadHello.start()
  threadBye.start()

  // Executors
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => (1 to 5).foreach(_ => println("Some from pool")))
  pool.execute(() => {
    Thread.sleep(1000)
    println("Done for one sec")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("Almost done")
    Thread.sleep(1000)
    println("Done for two sec")
  })
  pool.shutdown() // pool won't accept anymore actions
  // pool.shutdownNow() interrupts running and sleeping threads
}
