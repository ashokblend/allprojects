package org.apache.spark.examples

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.functions.{min, max}
import org.apache.spark.sql.Row
import scala.collection.mutable.ListBuffer

object MatrixMul {
    def main(args:Array[String]) {
      val file1=args(0)
      val file2=args(1)
      val fields=Array(StructField("rindx",IntegerType,false),
          StructField("cindx",IntegerType,true),
          StructField("value",IntegerType,true))
      val spark=SparkSession.builder().master("local").getOrCreate()
      val schema=StructType(fields)
          
      val df1 = spark.sqlContext.read.format("csv").option("header", false).schema(schema).load(file1)
      val df2= spark.sqlContext.read.format("csv").option("header", false).schema(schema).load(file2)
      
      val colCount= df1.agg(max(df1.col("cindx"))).collect()(0).get(0)
      val rowCount= df2.agg(max(df2.col("rindx"))).collect()(0).get(0)
      
      //compare dimension for feasibility of mulitiplication
      require(colCount==rowCount,s"Dimension mismatch: $colCount,$rowCount")
      //grouping each row data for first rdd
      val rd1=df1.rdd.map(row => {
        val key:Int = row.getInt(0)
        val value:List[Int]=List(row.getInt(2))
        key -> value
      })
      def reduceop(t1:List[Int], t2:List[Int]):List[Int]={
        val a=t1.++(t2)
        a
      }
      val rowData = rd1.reduceByKey(reduceop)
      
      //grouping each column data for second rdd
      val rd2=df2.rdd.map(row => {
        val key:Int = row.getInt(1)
        val value:List[Int]=List((row.getInt(2)))
        key -> value
      })
      val colData = rd2.reduceByKey(reduceop)
      
      val res1 = rowData.collect()
      val res2 = colData.collect()
      
      //Note this, join, will give us only digonal element of matrix
      //val diag = rowData.join(colData).collect
      
      val cart = rowData.cartesian(colData).groupByKey()
      val resultMatrix = cart.map(row =>{
        val rowId = row._1._1
        val rowData = row._1._2
        val colsData = row._2
        val result=ListBuffer[Int]()
        val mulData = colsData.foreach(x =>{
          val colId= x._1
          val colData = x._2
          var r:Int=0;
          for(i <- 0 until colData.length) {
            r=r+colData(i)*rowData(i)
          }
          result +=r
        })
        rowId -> result
      })
      println("Result of matrix multiplication:")
      val result = resultMatrix.sortByKey(true, 3).collect()
      result.foreach(row => {
        row._2.foreach(value =>{
          print(value +"  ")
        })
        println
        
      })
    }
}