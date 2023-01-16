package fish.genius.lambda.sbt

import org.scalatest.flatspec.AnyFlatSpec
import sbt.IO

import java.io.File
import java.nio.file.Files

class GraalVMLambdaCodeAssetBuilderSpec extends AnyFlatSpec {
  it should "include only the binary and the bootstrap if no resource directories are specified" in {
    val binaryFile = new File("sample/theBinary")
    val targetDirectory = Files.createTempDirectory("lambda").toFile
    val zipFile = GraalVMLambdaCodeAssetBuilder.lambdaZip(
      sourceFile = binaryFile,
      targetDirectory = targetDirectory
    )
    assert(zipFile.isDefined)
    assert(zipFile.forall(f => {
      val unzippedDirectory = Files.createTempDirectory("unzipped").toFile
      IO.unzip(f, unzippedDirectory)
      IO.listFiles(unzippedDirectory)
        .forall(u => u.getName == "bootstrap" || u.getName == "lambda")
    }))
  }

  it should "contain the resources directory tree when specified" in {
    val binaryFile = new File("sample/theBinary")
    val resourcesDirectory = new File("sample/resources")
    val targetDirectory = Files.createTempDirectory("lambda").toFile
    val zipFile = GraalVMLambdaCodeAssetBuilder.lambdaZip(
      sourceFile = binaryFile,
      targetDirectory = targetDirectory,
      sourceDirectories = List(resourcesDirectory)
    )
    assert(zipFile.isDefined)
    val unzippedDirectory = zipFile.map(f => {
      val unzippedDirectory = Files.createTempDirectory("unzipped").toFile
      IO.unzip(f, unzippedDirectory)
      unzippedDirectory
    })
    assert(unzippedDirectory.forall(f => {
      IO.listFiles(f)
        .forall(u =>
          u.getName == "bootstrap" || u.getName == "lambda" || u.getName == "conf" || u.getName == "images"
        )
    }))
    assert(unzippedDirectory.forall(f => {
      new File(f, "conf").isDirectory && new File(
        f,
        "images"
      ).isDirectory && new File(f, "conf/application.conf").canRead
    }))
  }
}
