package cpslab.entity

class VectorBucketMap(id: Int, bucketIds: Array[Int]) {
  override def toString(): String = {
    id + "," + bucketIds.toList
  }
}
