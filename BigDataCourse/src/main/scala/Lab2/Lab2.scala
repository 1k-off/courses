package Lab2

import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{
  Binarizer, Bucketizer, OneHotEncoder, Tokenizer,
  StopWordsRemover, HashingTF, RFormula, IDF, StringIndexer,
  OneHotEncoderEstimator, Word2Vec, MinMaxScaler, StandardScaler
}
import org.apache.spark.ml.linalg.Vectors

object Lab2 {
  val conf = new SparkConf().setAppName("Lab2").setMaster("local[*]")
  val spark = SparkSession.builder().config(conf).appName("Lab2").master("local").getOrCreate()
  import spark.implicits._

  def Listing11(): Unit ={
    println("Listing 1-1")
    val arrival_data = spark.createDataFrame(Seq(
      ("Lexus", "GX460 ", 100, 56700.0),
      ("Kia", "Niro EV", 95, 39450.0),
      ("Toyota", "Prius Prime", 93, 32019.0),
      ("Cadillac", "XT5 ", 89, 49995.0),
      ("Mazda", "MX-5 Miata", 88, 35350.0))).toDF("origin", "model", "rating", "price")
    val binarizer = new Binarizer().setInputCol("price")
      .setOutputCol("acceptable")
      .setThreshold(45000)
    binarizer.transform(arrival_data).show
    binarizer.transform(arrival_data).select("origin", "price", "acceptable").show
  }

  def Listing12(): Unit = {
    println("Listing 1-2")
    val arrival_data = spark.createDataFrame(Seq(
      ("Lexus", "GX460 ", 100, 56700.0),
      ("Kia", "Niro EV", 95, 39450.0),
      ("Toyota", "Prius Prime", 93, 32019.0),
      ("Cadillac", "XT5 ", 89, 49995.0),
      ("Mazda", "MX-5 Miata", 88, 35350.0))).toDF("origin", "model", "rating", "price")
    val bucketBorders = Array(80.0, 85.0, 95.0, 100.0)
    val bucketer = new Bucketizer().setSplits(bucketBorders)
      .setInputCol("rating")
      .setOutputCol("rank")
    val output = bucketer.transform(arrival_data)
    output.select("origin", "rating", "rank").orderBy("rating").show
  }

  def Listing13(): Unit = {
    println("Listing 1-3")
    val student_major_data = spark.createDataFrame(Seq(("Jim", "Math", 3),
      ("John", "Big Data", 7),
      ("Jeff", "DevOps", 3),
      ("Jane", "WEB design", 1),
      ("Julia", "IIoT", 9) )).toDF("user", "major", "majorIdx")
    val oneHotEncoder = new OneHotEncoder().setInputCol("majorIdx").setOutputCol("majorVect")
    oneHotEncoder.transform(student_major_data).show()
  }

  def Listing14(): Unit = {
    println("Listing 1-4")
    val text_data = spark.createDataFrame(Seq(
      (1, "Lorem ipsum dolor sit amet"),
      (2, "consectetur adipiscing elit"),
      (3, "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"),
      (4, "Ut enim ad minim veniam") )
    ).toDF("id", "line")
    val tokenizer = new Tokenizer().setInputCol("line").setOutputCol("words")
    val tokenized = tokenizer.transform(text_data)
    tokenized.select("words").withColumn("tokens", size(col("words"))).show(false)
  }

  def Listing15(): Unit = {
    println("Listing 1-5")
//    val stopWords = StopWordsRemover.loadDefaultStopWords("english")
    val stopWords = Array("intelektualna", "vlasnist", "bad")
    val remover = new StopWordsRemover().setStopWords(stopWords)
      .setInputCol("words")
      .setOutputCol("filtered")

    val text_data = spark.createDataFrame(Seq(
      (1, "Lorem ipsum dolor sit amet"),
      (2, "consectetur adipiscing elit"),
      (3, "sed do eiusmod tempor intelektualna vlasnist ut labore et dolore magna aliqua"),
      (4, "Ut enim ad minim veniam"))
    ).toDF("id", "line")
    val tokenizer = new Tokenizer().setInputCol("line").setOutputCol("words")
    val tokenized = tokenizer.transform(text_data)
    val cleanedTokens = remover.transform(tokenized)
    cleanedTokens.select("words","filtered").show(false)
  }

  def Listing16(): Unit = {
    println("Listing 1-6")
    //    val stopWords = StopWordsRemover.loadDefaultStopWords("english")
    val stopWords = Array("intelektualna", "vlasnist", "bad")
    val remover = new StopWordsRemover().setStopWords(stopWords)
      .setInputCol("words")
      .setOutputCol("filtered")
    val text_data = spark.createDataFrame(Seq(
      (1, "Lorem ipsum dolor sit amet"),
      (2, "consectetur adipiscing elit"),
      (3, "sed do eiusmod tempor intelektualna vlasnist ut labore et dolore magna aliqua"),
      (4, "Ut enim ad minim veniam"))
    ).toDF("id", "line")
    val tokenizer = new Tokenizer().setInputCol("line").setOutputCol("words")
    val tokenized = tokenizer.transform(text_data)
    val cleanedTokens = remover.transform(tokenized)
    cleanedTokens.select("words", "filtered").show(false)
    val tf = new HashingTF().setInputCol("filtered")
      .setOutputCol("TFOut")
      .setNumFeatures(4096)
    val tfResult = tf.transform(cleanedTokens)
    tfResult.select("filtered", "TFOut").show(false)
  }

  /*
  ~ separate target and terms
  + concat terms, "+ 0" means removing intercept
  - remove a term, "- 1" means removing intercept
  : interaction (multiplication for numeric values, or binarized categorical values)
  . all columns except target
  * factor crossing, includes the terms and interactions between them
  ^ factor crossing to a specified degree
   */

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing21(): Unit = {
    println("Listing 2-1")
    val arrival_data = spark.createDataFrame(Seq(
      ("Lexus", "GX460 ", 100, 56700.0, "yes"),
      ("Kia", "Niro EV", 95, 39450.0, "no"),
      ("Toyota", "Prius Prime", 93, 32019.0, "yes"),
      ("Cadillac", "XT5 ", 89, 49995.0, "no"),
      ("Mazda", "MX-5 Miata", 88, 35350.0, "yes"))).toDF("origin", "model", "rating", "price", "preference")
    val formula = new RFormula().setFormula("origin ~ . + price:rating").setFeaturesCol("features").setLabelCol("label")
    val output = formula.fit(arrival_data).transform(arrival_data)
    output.select("*").show(false)
  }

  def Listing22(): Unit = {
    println("Listing 2-2")
    val text_data = spark.createDataFrame(Seq(
      (1, "Lorem ipsum dolor sit amet"),
      (2, "consectetur adipiscing elit"),
      (3, "sed do eiusmod tempor intelektualna vlasnist ut labore et dolore magna aliqua"),
      (4, "Ut enim ad minim veniam"))
    ).toDF("id", "line")
    val tokenizer = new Tokenizer().setInputCol("line").setOutputCol("words")
    val tf = new HashingTF().setInputCol("words")
      .setOutputCol("wordFreqVect")
      .setNumFeatures(4096)
    val tfResult = tf.transform(tokenizer.transform(text_data))
    val idf = new IDF().setInputCol("wordFreqVect").setOutputCol("features")
    val idfModel = idf.fit(tfResult)
    val weightedWords = idfModel.transform(tfResult)
    weightedWords.select("wordFreqVect", "features").show(false)
  }

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing23(): Unit = {
    println("Listing 2-3")
    val cars_data = spark.createDataFrame(Seq(
      (1, "Lexus", "GT 460"),
      (2, "Audi", "Q7"),
      (3, "Infiniti", "QX70"),
      (4, "Mazda", "CX-5"),
      (5, "Audi", "Q5"),
      (6, "Kia", "Sportage"))).toDF("id", "origin", "model")
    val carsIndexer = new StringIndexer().setInputCol("origin").setOutputCol("originIdx")
    val carsIndexModel = carsIndexer.fit(cars_data)
    val indexedCars = carsIndexModel.transform(cars_data)
    indexedCars.orderBy("originIdx").show()
  }

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing24(): Unit = {
    println("Listing 2-4")
    val cars_data = spark.createDataFrame(Seq(
      (1, "Lexus", "GT 460"),
      (2, "Audi", "Q7"),
      (3, "Infiniti", "QX70"),
      (4, "Mazda", "CX-5"),
      (5, "Audi", "Q5"),
      (6, "Kia", "Sportage"))).toDF("id", "origin", "model")
    val carsIndexer = new StringIndexer().setInputCol("origin").setOutputCol("originIdx")
    val carsIndexModel = carsIndexer.fit(cars_data)
    val indexedCars = carsIndexModel.transform(cars_data)
    val oneHotEncoderEst = new OneHotEncoderEstimator().setInputCols(Array("originIdx")).setOutputCols(Array("originIdxVector"))
    val oneHotEncoderModel = oneHotEncoderEst.fit(indexedCars)
    val oneHotEncoderVect = oneHotEncoderModel.transform(indexedCars)
    oneHotEncoderVect.orderBy("origin").show()
  }

  // DOES NOT WORK WITH scala 2.11, java 11, spark 2.4.7
  // works with bitnami/spark:2.4.4-r47
  def Listing25(): Unit = {
    println("Listing 2-5")
    val documentDF = spark.createDataFrame(Seq(
      "Lorem ipsum dolor sit amet".split(" "),
      "consectetur Lorem elit".split(" "),
      "sed do eiusmod tempor intelektualna vlasnist ut labore et dolore elit aliqua".split(" ")).map(Tuple1.apply)).toDF("word")
    val word2Vec = new Word2Vec().setInputCol("word").setOutputCol("feature") .setVectorSize(3).setMinCount(0)
    val model = word2Vec.fit(documentDF)
    val result = model.transform(documentDF)
    result.show(false)
    model.findSynonyms("elit", 3).show
    model.findSynonyms("Lorem", 3).show
  }

  def Listing26(): Unit = {
    println("Listing 2-6")
    val employee_data = spark.createDataFrame(Seq(
      (1, Vectors.dense(123456, 9.8)),
      (2, Vectors.dense(234567, 7.6)),
      (3, Vectors.dense(345678, 5.4)),
      (4, Vectors.dense(456789, 3.2))))
      .toDF("empId", "features")
    val minMaxScaler = new MinMaxScaler().setMin(0.0)
      .setMax(5.0)
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
    val scalerModel = minMaxScaler.fit(employee_data)
    val scaledData = scalerModel.transform(employee_data)
    println(s"Features scaled to range: [${minMaxScaler.getMin},${minMaxScaler.getMax}]")
    scaledData.select("features", "scaledFeatures").show(false)
  }

  def Listing27(): Unit = {
    println("Listing 2-7")
    val employee_data = spark.createDataFrame(Seq(
      (1, Vectors.dense(123456, 9.8)),
      (2, Vectors.dense(234567, 7.6)),
      (3, Vectors.dense(345678, 5.4)),
      (4, Vectors.dense(456789, 3.2))))
      .toDF("empId", "features")
    val standardScaler = new StandardScaler()
      .setWithStd(true)
      .setWithMean(true)
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
    val standardMode = standardScaler.fit(employee_data)
    val standardData = standardMode.transform(employee_data)
    standardData.show(false)
  }

  def main(): Unit = {
    Listing11()
    Listing12()
    Listing13()
    Listing14()
    Listing15()
    Listing16()
//    Listing21()
    Listing22()
//    Listing23()
//    Listing24()
//    Listing25()
    Listing26()
    Listing27()
  }
}
