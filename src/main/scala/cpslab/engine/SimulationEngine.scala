package cpslab.engine

import scala.collection.mutable

import cpslab.event.Event

object SimulationEngine {
  val eventQueue = new mutable.PriorityQueue[Event]()(Ordering.by(evt => evt.timeStamp))
}
