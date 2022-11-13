package Lab4

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.evaluation.RegressionEvaluator

object Lab4 {
  val conf = new SparkConf().setAppName("Lab2").setMaster("local[*]")
  val spark = SparkSession.builder().config(conf).appName("Lab2").master("local").getOrCreate()
  import spark.implicits._

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing1(): Unit = {

    val dataDF = spark.read.option("header", "true").option("inferSchema", "true").csv("data/ratings9.csv")
    dataDF.printSchema
    dataDF.show
    val Array(trainingData, testData) = dataDF.randomSplit(Array(0.7, 0.3))
    val als = new ALS().setMaxIter(15).setRank(10).setSeed(1234).setRatingCol("rating").setUserCol("userId").setItemCol("movieId")
    val model = als.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.printSchema
    predictions.show
    val evaluator = new RegressionEvaluator().setPredictionCol("prediction").setLabelCol("rating").setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    val predictions2 = predictions.na.drop
    predictions2.show
    val evaluator2 = new RegressionEvaluator().setPredictionCol("prediction").setLabelCol("rating").setMetricName("rmse")
    val rmse2 = evaluator2.evaluate(predictions2)
    model.recommendForAllUsers(3).show(false)
    model.recommendForAllItems(3).show(false)
    model.recommendForItemSubset(Seq((111), (202), (225), (347), (488)).toDF("movieId"), 3).show(false)
    model.recommendForUserSubset(Seq((111), (100), (110), (120), (130)).toDF("userId"), 3).show(false)
  }

  def main(): Unit = {
    Listing1()
  }
}
