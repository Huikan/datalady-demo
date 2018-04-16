scalaVersion := "2.11.11"


val sparkVersion = "2.3.0" // provided by hdp

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion excludeAll ExclusionRule(organization = "log4j"),
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion ,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.2.0",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "log4j" % "log4j" % "1.2.17"
)


