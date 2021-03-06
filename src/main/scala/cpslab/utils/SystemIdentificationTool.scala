package cpslab.utils

import akka.actor.{Actor, ActorSystem, Props, ReceiveTimeout}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.Random


case class Response(vector: Int, nn: List[Int])

class ReceiverActor(k: Int) extends Actor {

  override def receive: Receive = {
    case vector: SparseVector =>
      sender() ! Response(vector.vectorId, (0 until k).map(i => Random.nextInt()).toList)
  }
}

class SenderActor(vectors:  Seq[SparseVector], remoteAddress: String) extends Actor {

  val startTime = new mutable.HashMap[Int, Long]
  val endTime = new mutable.HashMap[Int, Long]

  val remoteActor = context.actorSelection(remoteAddress)

  context.setReceiveTimeout(60000 milliseconds)

  override def preStart(): Unit = {
    var t = 0
    for (vector <- vectors) {
      vector.vectorId = t
      t += 1
      startTime += vector.vectorId -> System.currentTimeMillis()
      remoteActor ! vector
    }
  }

  override def postStop(): Unit = {
    val elapseTime = {
      for ((vId, endMoment) <- endTime) yield endTime(vId) - startTime(vId)
    }.toList
    val max = elapseTime.max
    val min = elapseTime.min
    val average = elapseTime.sum / elapseTime.size
    println(s"network RTT: $max, $min, $average, size: ${elapseTime.size}")
  }

  override def receive: Actor.Receive = {
    case resp: Response =>
      endTime += resp.vector -> System.currentTimeMillis()
    case ReceiveTimeout =>
      context.stop(self)
  }
}


object SystemIdentificationTool {

  private def buildVectorList(filePath: String, vectorNum: Int): Seq[SparseVector] = {

    val allFilePath = CommonUtils.buildFileListUnderDirectory(filePath)

    (for (filePath <- allFilePath; line <- Source.fromFile(filePath).getLines())
      yield Vectors.fromStringWithoutVectorID(line)).
      map(propertyTuple => new SparseVector(0, propertyTuple._1, propertyTuple._2, propertyTuple._3)).take(vectorNum)
  }

  private def identifyVectorComputation(vectors: Seq[SparseVector]): Long = {
    val startTime = System.nanoTime()
    val queryVector = vectors.head
    for (vector <- vectors) SimilarityCalculator.fastCalculateSimilarity(queryVector, vector)
    val endTime = System.nanoTime()
    endTime - startTime
  }

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      val actorSystem = ActorSystem("actorSystem", ConfigFactory.parseString(
        s"""
           |akka.actor.provider="akka.remote.RemoteActorRefProvider"
           |akka.remote.netty.tcp.hostname=192.168.55.148
           |akka.remote.netty.tcp.port=2552
      """.stripMargin))
      //start receiver actor
      actorSystem.actorOf(Props(new ReceiverActor(10)), name = "receiver")
    } else {
      val actorSystem = ActorSystem("actorSystem", ConfigFactory.parseString(
        s"""
           |akka.actor.provider="akka.remote.RemoteActorRefProvider"
           |akka.remote.netty.tcp.hostname=192.168.55.147
           |akka.remote.netty.tcp.port=2552
      """.stripMargin))

      val path = args(0)
      val vectorNum = args(1).toInt

      //identify vector computation
      val vectorList = buildVectorList(path, vectorNum)
      val elapseTime = identifyVectorComputation(vectorList)
      println("====Vector Similarity Computation====")
      println(s"vectorNum $vectorNum, time: ${elapseTime / 1000000} milliseconds")

      //identify network RTT

      //start sender
      actorSystem.actorOf(Props(new SenderActor(vectorList.take(vectorNum),
        "akka.tcp://actorSystem@192.168.55.148:2552/user/receiver")))
    }
  }
}
