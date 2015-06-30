package cpslab.utils

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.{Input, Output}
import org.objenesis.strategy.StdInstantiatorStrategy

object KryoUtils {

  def main(args: Array[String]): Unit = {

    val kryo = new Kryo()
    //val sparseVectorSerializer = new FieldSerializer(kryo, classOf[SparseVector])
    //val hashMapSerializer = new MapSerializer
    //sparseVectorSerializer.getField("indexToMap").setClass(classOf[mutable.HashMap[_, _]], hashMapSerializer)
    kryo.register(classOf[SparseVector])
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
    //vector object
    val indices = Array.fill[Int](2)(0)
    indices(1) = 2
    val values = Array.fill[Double](2)(0.0)
    values(0) = 1.0
    values(1) = 2.0
    val v = new SparseVector(1, 3, indices, values)
    val outputStream = new Output(1024*1000)
    kryo.writeObject(outputStream, v)
    val inputStream = new Input(outputStream.getBuffer)
    val v1 = kryo.readObject(inputStream, classOf[SparseVector])
    assert(v.size == v1.size)
    assert(v.vectorId == v1.vectorId)
    assert(v.indices.deep == v1.indices.deep, s"${v.indices.toList}, ${v1.indices.toList}")
    assert(v.values.deep == v1.values.deep, s"${v.values.toList}, ${v1.values.toList}")
  }

}
