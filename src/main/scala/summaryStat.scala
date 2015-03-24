/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.sparkwordcount

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{ Vector, Vectors }
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.stat.Statistics._
import org.apache.spark.mllib.stat.{ MultivariateStatisticalSummary, Statistics }
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.mllib.random.RandomRDDs._

object summaryStat {
  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setAppName("summary statistics"))

    //TASK 1 - summary statistics
    val data = sc.textFile(args(0))
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    // Compute column summary statistics.
    val summary: MultivariateStatisticalSummary = Statistics.colStats(parsedData)
    println("mean" + summary.mean) // a dense vector containing the mean value for each column
    println("variance" + summary.variance) // column-wise variance
    println("nonZeros" + summary.numNonzeros) // number of nonzeros in each column

    //TASK 2 - compute correlation
    val data1 = sc.textFile(args(1))
    val series1 = data1.map(s => s.toDouble).cache()

    val data2 = sc.textFile(args(2))
    val series2 = data2.map(s => s.toDouble).cache()

    val correlation: Double = Statistics.corr(series1, series2, "pearson")

    println("pearson correlation for two series " + correlation)

    // calculate the correlation matrix using Pearson's method. Use "spearman" for Spearman's method.
    // If a method is not specified, Pearson's method will be used by default. 
    val correlMatrix: Matrix = Statistics.corr(parsedData, "pearson")
    println("pearson correlation for the matrix " + correlMatrix)

    //TASK 3 - Stratified sampling
    // Load and parse the data file
    // args(3): xxx/xxx/sample_svm_data.txt
    val data3 = sc.textFile(args(3))

    val kvdata = data3.map { line =>
      val parts = line.split(' ')
      (parts(0).toDouble, new DenseVector(parts.tail.map(x => x.toDouble).toArray))
    }

    val fractions: Map[Double, Double] = Map(0.0 -> 0.05, 1.0 -> 0.05) // specify the exact fraction desired from each key

    // Get an exact sample from each stratum
    val approxSample = kvdata.sampleByKey(withReplacement = false, fractions, 1L)
    val exactSample = kvdata.sampleByKeyExact(withReplacement = false, fractions, 1L)
    println("sampley by key results " + approxSample)
    println("sampley by keyExact results " + exactSample)

    //TASK 4 - Hypothesis testing
    val goodnessOfFitTestResult = Statistics.chiSqTest(Vectors.dense(1.0, 0.0, 3.0, 3.0))
    println("goodness of fit test result " + goodnessOfFitTestResult)

    val independenceTestResult = Statistics.chiSqTest(correlMatrix)
    println("independence test result " + independenceTestResult) // summary of the test including the p-value, degrees of freedom...

    val obs = data3.map { line =>
      val parts = line.split(' ')
      LabeledPoint(parts(0).toDouble, new DenseVector(parts.tail.map(x => x.toDouble).toArray))
    }

    // The contingency table is constructed from the raw (feature, label) pairs and used to conduct
    // the independence test. Returns an array containing the ChiSquaredTestResult for every feature 
    // against the label.
    val featureTestResults = Statistics.chiSqTest(obs)
    var i = 1
    featureTestResults.foreach { result =>
      println(s"Column $i:\n$result")
      i += 1
    } // summary of the test

    //TASK 5 - Random Data Generation
    // Generate a random double RDD that contains 1 million i.i.d. values drawn from the
    // standard normal distribution `N(0, 1)`, evenly distributed in 10 partitions.
    //val u = normalRDD(sc, 1000000L, 10)
    val u = normalRDD(sc, 100L, 3)
    // Apply a transform to get a random double RDD following `N(1, 4)`.
    val v = u.map(x => 1.0 + 2.0 * x)
    v.saveAsTextFile(args(4))

  }
}
