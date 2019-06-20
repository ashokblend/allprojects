package org.apache.spark.examples
import java.io.{BufferedWriter, File, FileWriter}

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import scala.io.Source


object TestSparkSql  {
  def main(args: Array[String]) {
   
    val file: File = new File(args(0))
    val basePath: String = file.getParentFile.getAbsolutePath

    // get current directory:/examples
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "../../../")
      .getCanonicalPath

    // specify parameters
    val storeLocation = basePath + "/store"
    val warehouse = s"$storeLocation/warehouse"
    val metastoredb = s"$storeLocation/metastore_db"
    // clean data folder
    if (false) {
      val clean = (path: String) => FileUtils.deleteDirectory(new File(path))
      clean(storeLocation)
      clean(warehouse)
      clean(metastoredb)
    }

    val spark = SparkSession
      .builder()
      .master("local[1]")
      //.master("yarn")
      .appName("SparkExample")
      .enableHiveSupport()
     // .config("spark.yarn.archive","/Users/ashok.kumar/github/spark/assembly/target/spark-archive.zip")
      .config("spark.sql.warehouse.dir", warehouse)
      .config("spark.driver.extraJavaOptions","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5004 -Dlog4j.configuration=/Users/ashok.kumar/github/spark/conf/log4j.properties")
      .config("spark.executor.extraJavaOptions","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
      .config("hive.exec.scratchdir","/Users/ashok.kumar/github/workspace/spark/store/tmp/hive/")
      //.config("spark.sql.shuffle.partitions",3)
      .config("spark.driver.memory","1G")
      .config("spark.executor.instances","1")
      .config("spark.dynamicAllocation.enabled","false")
      .config("spark.executor.memory","3G")
      .config("spark.sql.codegen.wholeStage","true")
      .config("spark.driver.memory","512m")
      .config("spark.network.timeout","200m")
      .config("spark.network.timeoutInterval","100m")
      .config("spark.executor.heartbeatInterval","100m")
      .config("orc.stripe.size","134217728")
      .config("javax.jdo.option.ConnectionURL",
        s"jdbc:derby:;databaseName=$metastoredb;create=true")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")


    val sqlFile = new File(basePath + File.separator + "test_spark.sql")
    var time = sqlFile.lastModified()
    var isModified = true
    val thread = new Thread {
      while(true) {
        val resultFile = new File(basePath + File.separator + "result.xt")
        var bw: BufferedWriter = null
        if(isModified) {
            try {
              resultFile.delete();
              bw = new BufferedWriter(new FileWriter(resultFile, true));
              Source.fromFile(sqlFile).getLines().foreach { x => processCommand(x, spark, bw) }
            } catch {
              case ex:Exception => ex.printStackTrace()
            } finally  {
              if (null!= bw) {
                bw.close
              }
            }
          }
          val modifiedTime= sqlFile.lastModified()
          if(time == modifiedTime) {
            isModified=false
          } else {
            isModified = true
            time = modifiedTime
          }
       
      }
    }
    
  }
  def processCommand(cmd: String, spark: SparkSession, bw: BufferedWriter): Unit = {
    // scalastyle:off
    if (!cmd.startsWith("#") && cmd.trim().length() > 0) {
      println(s"executing>>>>>>$cmd")
      bw.write(s"executing>>>>>>$cmd")
      bw.newLine()
      val result = spark.sql(cmd)
      result.collect().foreach{ row =>
        val record = row.mkString(",")
        println(record)
        bw.write(record)
        bw.write("\n")
      }
      //result.show(100, truncate=false)
      println(s"executed>>>>>>$cmd")
      bw.write(s"executing>>>>>>$cmd")
      bw.newLine()
      bw.flush()
    }
  }
}
