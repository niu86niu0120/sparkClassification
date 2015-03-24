import AssemblyKeys._ 

assemblySettings 

name := "WordCount" 

version := "1.0"

scalaVersion := "2.11.5" 

libraryDependencies ++= Seq(
"org.apache.spark" % "spark-core_2.10" % "1.2.0" % "provided",
"org.scala-lang" % "scala-library" % "2.11.5" % "provided", 
"com.google.collections" % "google-collections" % "1.0-rc2" % "provided",
"org.apache.spark" % "spark-mllib_2.10" % "1.2.0" % "provided"
) 

mainClass in assembly := Some("WordCount") 

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => { 
case entry => { 
val strategy = mergeStrategy(entry)
if (strategy == MergeStrategy.deduplicate) MergeStrategy.first 
else strategy 
} 
}}


