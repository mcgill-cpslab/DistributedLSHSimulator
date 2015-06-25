package cpslab.engine

import cpslab.event.Event

import scala.collection.mutable

object SimulationEngine {
  val eventQueue = new mutable.PriorityQueue[Event]()(Ordering.by(evt => evt.timeStamp))

  def main(args: Array[String]): Unit = {
    val vectorParser = new VectorParser(args(0))
    println(vectorParser.parse().toList.take(5))
    println("=========")

    val lshParser = new LSHParser(args(1))
    println(lshParser.parse().toList)
  }
}
