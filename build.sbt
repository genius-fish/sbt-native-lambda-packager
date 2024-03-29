ThisBuild / organization := "fish.genius"
ThisBuild / organizationName := "Genius Fish"
ThisBuild / organizationHomepage := Some(url("https://genius.fish"))
ThisBuild / scalaVersion := "2.12.12"
ThisBuild / resolvers += Resolver.mavenLocal
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / developers := List(
  Developer(
    id = "jlust",
    name = "Jurgen Lust",
    email = "jurgen@genius.fish",
    url = url("https://genius.fish")
  )
)
ThisBuild / publishTo := Some(
  "Maven Repo" at "https://maven.pkg.github.com/genius-fish/sbt-native-lambda-packager"
)
ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  sys.env.getOrElse("GITHUB_PACKAGES_OWNER", "none"),
  sys.env.getOrElse("GITHUB_PACKAGES_TOKEN", "none")
)
ThisBuild / sbtPlugin := true
ThisBuild / sbtVersion := "1.3.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sbt-native-lambda-packager",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.16",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
  )
  .enablePlugins(SbtPlugin)
