package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {
  /* PRODUCER-CONSUMER Problem
  * producer -> [ x ] -> consumer
  * Force consumer to wait for producer
  */

  class SimpleContainer{
    private var value:Int = 0
    def isEmpty:Boolean = value == 0
    def set(newValue:Int):Unit = value = newValue
    def get:Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons():Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting ...")
      while (container.isEmpty)
        println("[consumer] actively waiting ...")

      println("[consumer] I have consume " + container.get)
    })
    val producer = new Thread(() => {
      println("[producer] computing ..")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produce " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }
  //naiveProdCons()

  // Wait and notify
  def smartProdCons() = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting")
      container.synchronized{
        container.wait()
      }

      // at this point the container must have a value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] Hard work ... ")
      Thread.sleep(2000)
      val value = 42

      container.synchronized{
        println("[producer] I am producing " + value)
        container.set(value)
        container.notify()
      }
    })
    consumer.start()
    producer.start()
  }
  //smartProdCons()

  /*
  * Producer produce many values, consumer consume indefinitely
  * */
  def largeBufferProdCons():Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true){
        buffer.synchronized{
          if(buffer.isEmpty) {
            println("[consumer] buffer empty, waiting")
            buffer.wait()
          }

          // buffer has a value
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while(true){
        buffer.synchronized{
          if(buffer.size == capacity){
            println("[producer] buffer is full, waiting")
            buffer.wait()
          }

          // at least one empty space
          println("[producer] produce " + i)
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(250))
      }
    })

    consumer.start()
    producer.start()
  }

  //largeBufferProdCons()

  /*
   MULTIPLE CONSUMER MULTIPLE PRODUCER
  * */
  def largeBufferMultipleProdCons():Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20
    var i = 0

    val consumers = (0 to 5).map(ci => new Thread(() => {
      val random = new Random()
      while(true){
        buffer.synchronized{
          while(buffer.isEmpty) {
            println(s"[consumer $ci] buffer empty, waiting")
            buffer.wait()
          }

          // buffer has a value
          val x = buffer.dequeue()
          println(s"[consumer $ci] consumed " + x)
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }))

    val producers = (0 to 5).map(pi => new Thread(() => {
      val random = new Random()
      while(true){
        buffer.synchronized{
          while (buffer.size == capacity){
            println(s"[producer $pi] buffer is full, waiting")
            buffer.wait()
          }

          // at least one empty space
          println(s"[producer $pi] produce " + i)
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(250))
      }
    }))

    consumers.foreach(_.start())
    producers.foreach(_.start())
  }

  largeBufferMultipleProdCons()
}
