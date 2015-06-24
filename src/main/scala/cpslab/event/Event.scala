package cpslab.event

class Event(val timeStamp: Long)

case class SendRequestEvent(t: Long) extends Event(t)

case class ProcessRequestEvent(t: Long) extends Event(t)

