package fish.genius.lambda.sbt

import com.typesafe.sbt.packager.graalvmnativeimage.GraalVMNativeImagePlugin
import com.typesafe.sbt.packager.graalvmnativeimage.GraalVMNativeImagePlugin.autoImport.GraalVMNativeImage
import sbt.Keys.*
import sbt.{AutoPlugin, Compile, File, settingKey, taskKey}

object NativeLambdaZipPlugin extends AutoPlugin {
  override def requires = GraalVMNativeImagePlugin
  override def trigger = allRequirements

  object autoImport {
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
          (GraalVMNativeImage / packageBin).value,
          includeDirectories.value.toList,
          (Compile / target).value
        )
        .getOrElse((GraalVMNativeImage / packageBin).value)
    },
    lambdaZip / includeDirectories := Nil
  )
}
