import AssemblyKeys._ 

assemblySettings 

name := "Recommender" 

version := "1.0"

scalaVersion := "2.10.4" 

libraryDependencies ++= Seq(
"org.apache.spark" % "spark-core_2.10" % "1.2.0" % "provided",
"org.scala-lang" % "scala-library" % "2.10.4", 
"com.google.collections" % "google-collections" % "1.0-rc2" % "provided",
"org.apache.spark" % "spark-mllib_2.10" % "1.2.0" % "provided",
"org.apache.logging.log4j" % "log4j-api" % "2.1"
) 

mainClass in assembly := Some("Recommender") 

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => { 
case entry => { 
val strategy = mergeStrategy(entry)
if (strategy == MergeStrategy.deduplicate) MergeStrategy.first 
else strategy 
} 
}}


