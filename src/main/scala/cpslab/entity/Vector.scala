package cpslab.entity

class SparseVector(id: Int, bucketIds: Array[Int]) {
  override def toString(): String = {
    id + "," + bucketIds.toList
  }
}
