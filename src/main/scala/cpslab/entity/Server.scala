package cpslab.entity

import scala.collection.mutable

class Server(processingQueueNum: Int) {
  //mapping from bucket id to vector number
  val maintainingBucketList = new mutable.HashMap[Int, Int]

  var freeCoreNum = 0

  val requestQueue = new mutable.Queue[Request]()
}

case class LSHTable(id: Int, buckets: Array[LSHBucket])

case class LSHBucket(id: Int, size: Int)
