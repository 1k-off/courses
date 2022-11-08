import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object Lab1 {
  val conf = new SparkConf().setAppName("Lab1").setMaster("local[*]")
  val spark = SparkSession.builder().config(conf).appName("Lab1").master("local").getOrCreate()
  //  val sc: SparkContext = spark.sparkContext
  import spark.implicits._


  case class Movie(actor_name:String, movie_title:String, produced_year:Long)

  def Listing11(): Unit ={
    println("Listing 1-1")
    val (dateUS, dateTimeUS, dateEU, dateEUInvalid) = ("2022-10-04", "2022-10-04 21:50:58:865", "04-10-2022", "04-10-2022 35:11")
    val testDateTSDF = Seq((1, dateUS, dateTimeUS, dateEU, dateEUInvalid)).
      toDF("id", "date", "timestamp", "date_str", "ts_str")
    val testDateResultDF = testDateTSDF.select(to_date('date).as("date1"), to_timestamp('timestamp).
      as("ts1"), to_date('date_str, "MM-dd-yyyy").as("date2"), to_timestamp('ts_str,
      "MM-dd-yyyy m:ss").as("ts2"), unix_timestamp('timestamp).as("unix_ts")).show(false)
    print(testDateResultDF)
  }

  def Listing12(): Unit ={
    println("Listing 1-2")
    val employeeData = Seq( ("Marty", "1985-10-26", "2015-10-21"),
      ("Emmett", "1885-09-02", "1985-07-07")).toDF("name", "join_date", "leave_date")
    employeeData.show
    employeeData.select('name, datediff('leave_date, 'join_date).as("days"),
      months_between('leave_date, 'join_date).as("months"),
      last_day('leave_date).as("last_day_of_mon")).show
    val oneDate = Seq(("2018-01-01")).toDF("new_year")
    oneDate.select(date_add('new_year, 14).as("mid_month"),
      date_sub('new_year, 1).as("new_year_eve"),
      next_day('new_year, "Mon").as("next_mon")).show
  }

  def Listing13(): Unit = {
    println("Listing 1-3")
    val valentimeDateDF = Seq(("2022-10-04 21:50:58")).toDF("date")
    valentimeDateDF.select(year('date).as("year"),
      quarter('date).as("quarter"),
      month('date).as("month"),
      weekofyear('date).as("woy"),
      dayofmonth('date).as("dom"),
      dayofyear('date).as("doy"),
      hour('date).as("hour"),
      minute('date).as("minute"),
      second('date).as("second")).show
  }

  def Listing21(): Unit = {
    println("Listing 2-1")
    val sparkDF = Seq((" kosar ")).toDF("name")
    sparkDF.select(trim('name).as("trim"),
      ltrim('name).as("ltrim"), rtrim('name).as("rtrim")).show
    sparkDF.select(trim('name).as("trim")).select(lpad('trim, 8, "-").as("lpad"),
      rpad('trim, 8, "=").as("rpad")).show
    val sparkAwesomeDF = Seq(("Spark", "is", "awesome")).toDF("subject", "verb", "adj")
    sparkAwesomeDF.select(concat_ws(" ", 'subject, 'verb,
      'adj).as("sentence")).select(lower('sentence).as("lower"), upper('sentence).as("upper"),
      initcap('sentence).as("initcap"), reverse('sentence).as("reverse")).show
    sparkAwesomeDF.select('subject, translate('subject, "ar", "oc").
      as("translate")).show
  }

  def Listing22(): Unit = {
    println("Listing 2-2")
    val rhymeDF = Seq(("The quick brown fox jumps over the lazy dog")).toDF("rhyme")
    rhymeDF.select(regexp_extract('rhyme, "[a-z]*o[xw]",0).as("substring")).show
  }

  def Listing23(): Unit = {
    println("Listing 2-3")
    val rhymeDF = Seq(("The quick brown fox jumps over the lazy dog")).toDF("rhyme")
    rhymeDF.select(regexp_replace('rhyme, "fox|crow", "animal").as("new_rhyme")).show(false)
    rhymeDF.select(regexp_replace('rhyme, "[a-z]*o[xw]", "animal").as("new_rhyme")).show(false)
  }

  def Listing31(): Unit = {
    println("Listing 3-1")
    val tasksDF = Seq(("Tuesday", Array("Check trello", "Prepare tasks for the team", "Fix autoscaling on project"))).toDF("day", "tasks")
    tasksDF.select('day, size('tasks).as("size"),
      sort_array('tasks).as("sorted_tasks"),
      array_contains('tasks, "Fix autoscaling on project").as("shouldFixAutoscaling")).show(false)
    tasksDF.select('day, explode('tasks)).show(false)
  }

  def Listing41(): Unit = {
    println("Listing 4-1")
    val numDF = spark.range(1,30,2,6)
    numDF.rdd.getNumPartitions
    numDF.select('id, monotonically_increasing_id().as("m_ii"),
      spark_partition_id().as("partition")).show
  }

  def Listing42(): Unit = {
    println("Listing 4-2")
    val dayOfWeekDF = spark.range(11,2,-1)
    dayOfWeekDF.select('id, when('id === 1, "Mon")
      .when('id === 2, "Tue")
      .when('id === 3, "Wed")
      .when('id === 4, "Thu")
      .when('id === 5, "Fri")
      .when('id === 6, "Sat")
      .when('id === 7, "Sun").as("dow")).show
    dayOfWeekDF.select('id, when('id === 6, "Weekend")
      .when('id === 7, "Weekend")
      .otherwise("Weekday").as("day_type")).show
  }

  def Listing43(): Unit = {
    println("Listing 4-3")
    val badMoviesDF = Seq(Movie("Vin Diesel", "Fast & Furious: Hobbs & Shaw", 2019),
      Movie("Samuel Leroy Jackson", "Glass", 2019)).toDF
    badMoviesDF.select(coalesce('actor_name, lit("no_name")).as("Actor")).show(false)

  }
  def main(): Unit = {
    Listing11()
    Listing12()
    Listing13()
    Listing21()
    Listing22()
    Listing23()
    Listing31()
    Listing41()
    Listing42()
    Listing43()
  }
}