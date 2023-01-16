package fish.genius.lambda.sbt

import sbt.io.IO

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.util.{Failure, Success, Try}

object GraalVMLambdaCodeAssetBuilder {
  def lambdaZip(sourceFile: File, sourceDirectories: List[File] = Nil, targetDirectory: File): Option[File] = for {
    workDir <- workDirectory
    binary <- binaryFile(sourceFile, workDir)
    bootstrap <- bootstrapFile(workDir)
    resources <- resourceFiles(workDir, sourceDirectories)
    uploadZip <- zip(workDir)
    result <- targetUploadZip(targetDirectory, uploadZip)
  } yield result

  def workDirectory: Option[File] = Try {
    Files.createTempDirectory("genius-fish-lambda").toFile
  }.toOption.map(wd => {
    println(s"work directory: ${wd}")
    wd
  })

  def binaryFile(
      sourceFile: File,
      workDir: File
  ): Option[File] = Try {
    val targetFile = new File(workDir, "lambda")
    IO.copyFile(sourceFile, targetFile)
    targetFile
  }.toOption

  def bootstrapFile(workDir: File): Option[File] = Try {
    val targetFile = new File(workDir, "bootstrap")
    IO.append(
      targetFile,
      """#!/usr/bin/env bash
        |
        |set -euo pipefail
        |
        |./lambda
        |
        |""".stripMargin,
      StandardCharsets.UTF_8
    )
    targetFile
  }.toOption

  def resourceFiles(workDir: File, directories: List[File]) = Try {
    directories.flatMap(d => {
      val files = IO.listFiles(d)
      files.flatMap {
        case f: File if f.isDirectory => {
          IO.copyDirectory(f, workDir)
          List(f)
        }
        case f: File if f.canRead => {
          IO.copyFile(f, new File(workDir, f.getName))
          List(f)
        }
        case f => {
          println(s"cannot copy ${f.getAbsolutePath}")
          Nil
        }
      }
    })
  } match {
    case Success(value) => Some(value)
    case Failure(cause) => {
      println(s"could not copy resources: ${cause.getMessage}")
      None
    }
  }

  def zip(workDir: File, files: List[File]): Option[File] = Try {
    val targetFile = new File(workDir, "upload.zip")
    IO.zip(files.map(f => (f, f.getName)), targetFile)
    targetFile
  }.toOption

  def targetUploadZip(targetDirectory: File, zipFile: File): Option[File] =
    Try {
      val distDirectory = new File(targetDirectory, "dist")
      val targetFile = new File(distDirectory, "native-lambda-upload.zip")
      IO.copyFile(zipFile, targetFile)
      println(s"created Lambda Zip file: ${targetFile.getAbsolutePath}")
      targetFile
    }.toOption

}
