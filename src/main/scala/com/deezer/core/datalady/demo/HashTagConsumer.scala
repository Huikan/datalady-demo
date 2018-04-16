package com.deezer.core.datalady.demo


import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import scalaj.http.Http


object HashTagConsumer extends App {

  override def main(args: Array[String]): Unit = {
    val errorMessage = "Twitter api secrets should be defined"
    require(Option(System.getProperty("twitter4j.oauth.consumerKey")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.consumerSecret")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.accessToken")).isDefined, errorMessage)
    require(Option(System.getProperty("twitter4j.oauth.accessTokenSecret")).isDefined, errorMessage)


    if (!Logger.getRootLogger.getAllAppenders.hasMoreElements) {
      Logger.getRootLogger.setLevel(Level.WARN)
    }

    //val filters = Seq.empty
    val filters = Seq("#datalady", "#womenintech")

    val sparkConf = new SparkConf().setAppName("TwitterPopularTags").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val stream = TwitterUtils.createStream(ssc, None, filters)

    val hashTags = stream.filter(_.getLang.contentEquals("en")).flatMap{
      status =>
        status.getText.trim.replace("\n", "")
          .split(" ")
          .filter(txt => txt.startsWith("#") && !txt.contains(".*(http|https|www.).*"))
    }

    val topCounts = hashTags
      .map((_, 1))
      .reduceByKeyAndWindow(_ + _, Minutes(10))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    topCounts.foreachRDD(
      rdd => {
        val windowRdd = rdd.take(20).toList

        if (windowRdd.nonEmpty) {
          val topics: String = windowRdd.map(tp =>  "\"" + tp._2 + "\"").mkString("[", ",", "]")
          val counts: String = windowRdd.map(tp =>  tp._1.toString ).mkString("[", ",", "]")

          println(topics)
          println(counts)
          // dashboard app as receiver
          println(Http("http://localhost:5000/updateData")
            .postForm(Seq(("label", topics), ("data", counts)))
            .header("Content-Type", "application/json")
            .header("Charset", "UTF-8").asString)
        }

    })

    ssc.start()
    ssc.awaitTermination()
    ssc.stop(true, true)
  }
}
