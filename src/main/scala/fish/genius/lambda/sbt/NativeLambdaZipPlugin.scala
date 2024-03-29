package fish.genius.lambda.sbt

import sbt.Keys._
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Compile, File, settingKey, taskKey}

object NativeLambdaZipPlugin extends AutoPlugin {
  override def requires = JvmPlugin
  override def trigger = noTrigger

  object autoImport {
    val binaryFile = settingKey[File]("the binary file that contains the logic of the lambda")
    val includeDirectories =
      settingKey[Seq[File]]("directories to include in the lambda zip file")
    val lambdaZip =
      taskKey[File]("create a zip file for the native Lambda function")
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    lambdaZip := {
      GraalVMLambdaCodeAssetBuilder
        .lambdaZip(
          (lambdaZip / binaryFile).value,
          (lambdaZip / includeDirectories).value.toList,
          (Compile / target).value
        )
        .getOrElse((lambdaZip / binaryFile).value)
    },
    lambdaZip / includeDirectories := Nil
  )
}
