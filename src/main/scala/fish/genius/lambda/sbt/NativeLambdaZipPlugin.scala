package fish.genius.lambda.sbt

import com.typesafe.sbt.packager.graalvmnativeimage.GraalVMNativeImagePlugin
import com.typesafe.sbt.packager.graalvmnativeimage.GraalVMNativeImagePlugin.autoImport.GraalVMNativeImage
import sbt.Keys._
import sbt.{AutoPlugin, Compile, File, taskKey}

object NativeLambdaZipPlugin extends AutoPlugin {
  override def requires = GraalVMNativeImagePlugin
  override def trigger = allRequirements

  object autoImport {
    val lambdaZip =
      taskKey[File]("create a zip file for the native Lambda function")
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    lambdaZip := {
      GraalVMLambdaCodeAssetBuilder
        .lambdaZip(
          (GraalVMNativeImage / packageBin).value,
          (Compile / target).value
        )
        .getOrElse((GraalVMNativeImage / packageBin).value)
    }
  )
}
