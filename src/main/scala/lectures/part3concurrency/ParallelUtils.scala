package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App{
  // 1. Parallel collections
  val parList = List(1,2,3).par

  val parVector = ParVector[Int](1,2,3)

  def measure[T](operation: => T): Long ={
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  // Small list are better to be run in sequential mode
  val list = (1 to 100000).toList
  val serialTime = measure {
    list.map(_+1)
  }
  println("serial time: " + serialTime)

  val parallelTime = measure {
    list.par.map(_+1)
  }
  println("parallel time: " + parallelTime)

  /*
  Parallel collection operate on map-reduce model
    - Split data into chunks
    - Chunks are processed into separate threads
    - Results are recombined with Combiner
  * */
  // map flatMap filter foreach reduce fold
  // Watch out for fold and reduce with non-associative operators
  println(List(1,2,3).reduce(_-_))
  println(List(1,2,3).par.reduce(_-_))

  var sum = 0
  List(1,2,3).par.foreach(sum += _)
  println(sum)

  // configuration for parallel collections
  parVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /* Alternatives:
   - ThreadPoolTaskSupport - deprecated
   - ExecutionContextTaskSupport
  */

  parVector.tasksupport = new TaskSupport {
    // Environment that manages the thread
    override val environment: AnyRef = ???

    // Schedules a thread to run a parallel
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    // Same as execute but blocks and waits until all threads are complete
    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    // Number of execution cores
    override def parallelismLevel: Int = ???
  }

  // Atomic operations and references
  val atomic = new AtomicReference[Int](2)
  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write
  atomic.getAndSet(5) // Thread safe combo
  atomic.compareAndSet(38, 56) // if 38, set to 56, compares reference equality
  atomic.updateAndGet(_+1) // Thread safe function run
  atomic.getAndUpdate(_+1)
  atomic.accumulateAndGet(12, _ + _) // adds 12 -> applies lambda -> gets result
  atomic.getAndAccumulate(12, _ + _)
}
