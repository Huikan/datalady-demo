package com.deezer.core.datalady.demo

import java.io.File

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext, Time}


object HashTagDataLadyFileSinkApp extends App {

  override def main(args: Array[String]): Unit = {

    val errorMessage = "Twitter api secrets should be defined"
    require(Option(System.getProperty("twitter4j.oauth.consumerKey")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.consumerSecret")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.accessToken")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.accessTokenSecret")).isDefined, errorMessage)


    println("Creating new context")
    val outputFile = new File("~/out")
    if (outputFile.exists()) outputFile.delete()
    val sparkConf = new SparkConf().setAppName("HashTagDataLadyFileSinkApp")
    sparkConf.setMaster("local[2]")
    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint1")

    val filters = Seq("#datalady", "#womenintech")

    val stream = TwitterUtils.createStream(ssc, None, filters)


    stream.map(status => status.getText)/*.countByWindow(Seconds(30), Seconds(30))*/.foreachRDD{
      (rdd: RDD[String], time: Time) =>
        println(s"$time : ${rdd.collect().mkString("\n ")} ")
    }

    ssc.start()
    ssc.awaitTermination()
    ssc.stop(stopSparkContext = true, stopGracefully = true)
  }
}
