package Lab3

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator


object Lab3 {
  val conf = new SparkConf().setAppName("Lab2").setMaster("local[*]")
  val spark = SparkSession.builder().config(conf).appName("Lab2").master("local").getOrCreate()

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing1(): Unit = {
    println("Listing 1")
    val irisSchema = StructType(Array(
      StructField("sepal_length", DoubleType, true),
      StructField("sepal_width", DoubleType, true),
      StructField("petal_length", DoubleType, true),
      StructField("petal_width", DoubleType, true),
      StructField("class", StringType, true)
    ))
    val dataDF = spark.read.format("csv").option("header", "false").schema(irisSchema).load("data/iris9.data")
    dataDF.show
    dataDF.describe().show(5,15)
    val labelIndexer = new StringIndexer().setInputCol("class").setOutputCol("label")
    val dataDF2 = labelIndexer.fit(dataDF).transform(dataDF)
    dataDF2.show
    val features = Array("sepal_length","sepal_width","petal_length", "petal_width")
    val assembler = new VectorAssembler().setInputCols(features).setOutputCol("features")
    val dataDF3 = assembler.transform(dataDF2)
    dataDF3.show
    dataDF3.stat.corr("petal_length","label")
    dataDF3.stat.corr("petal_width","label")
    dataDF3.stat.corr("sepal_length","label")
    dataDF3.stat.corr("sepal_width","label")
    val seed = 1234
    val Array(trainingData, testData) = dataDF3.randomSplit(Array(0.8, 0.2), seed)
    val lr = new LogisticRegression().setMaxIter(10000).setRegParam(0.01)
    val model = lr.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.select("sepal_length","sepal_width", "petal_length","petal_width","label","prediction").show
    predictions.select("rawPrediction","probability","prediction").show(false)
    val evaluator = new MulticlassClassificationEvaluator().setMetricName("f1")
    val f1 = evaluator.evaluate(predictions)
    val wp = evaluator.setMetricName("weightedPrecision").evaluate(predictions)
    val wr = evaluator.setMetricName("weightedRecall").evaluate(predictions)
    val accuracy = evaluator.setMetricName("accuracy").evaluate(predictions)

  }

  def main(): Unit = {
    Listing1()
  }
}
