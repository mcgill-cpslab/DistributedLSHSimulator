package cpslab.engine

import cpslab.entity.{LSHBucket, LSHTable, VectorBucketMap}
import cpslab.utils.CommonUtils

import scala.io.Source

class LSHParser(filePath: String) {

  import CommonUtils._

  private def getLSHTable(lineStr: String): LSHTable = {
    val array = lineStr.split("\\),")
    //get tableId
    val Array(tableId, firstBucketId, firstBucketSize) = array(0).replace("List((", "").replace("(", "").split(",")
    val allOtherBucketsStr = array.tail
    val allOtherBucketsArray = allOtherBucketsStr.map(str => {
      val Array(id, size) = str.replace("(", "").split(",").map(_.stripPrefix(" ").replace(")))", "").toInt)
      new LSHBucket(id, size)
    })
    new LSHTable(tableId.toInt, new LSHBucket(firstBucketId.toInt, firstBucketSize.toInt) +: allOtherBucketsArray)
  }

  def parse(): Seq[LSHTable] = {
    val allFilePaths = buildFileListUnderDirectory(filePath)

    for (file <- allFilePaths; line <- Source.fromFile(file).getLines()) yield getLSHTable(line)
  }
}

class VectorParser(filePath: String) {

  import CommonUtils._

  private def getVector(lineStr: String): VectorBucketMap = {
    val Array(vectorData, bucketIds) = lineStr.split("\\),")
    //vectorData
    val vectorId = vectorData.split(",")(0).replace("((","").toInt
    //bucketIds
    val bucketIdList = bucketIds.replace(")", "").split(",").map(_.toInt)
    new VectorBucketMap(vectorId, bucketIdList)
  }

  def parse(): Seq[VectorBucketMap] = {
    val allFilePaths = buildFileListUnderDirectory(filePath)

    for (file <- allFilePaths; line <- Source.fromFile(file).getLines()) yield getVector(line)
  }
}


