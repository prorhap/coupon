lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := "coupon",
    version := "1.0.0",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      javaWs,
      "com.h2database" % "h2" % "1.4.199",
      "org.hibernate" % "hibernate-core" % "5.4.9.Final",
      "io.dropwizard.metrics" % "metrics-core" % "4.1.1",
      "com.palominolabs.http" % "url-builder" % "1.1.0",
      "net.jodah" % "failsafe" % "2.3.1",
      "com.github.akarnokd" % "rxjava2-interop" % "0.13.7",
      "io.reactivex.rxjava2" % "rxjava" % "2.1.9",
      "com.spotify" % "folsom" % "1.0.0",
      "io.reactivex" % "rxjava-reactive-streams" % "1.2.0",
      "org.projectlombok" % "lombok" % "1.18.2",
      "org.apache.commons" % "commons-lang3" % "3.10",
      "org.apache.commons" % "commons-collections4" % "4.4",
      "org.modelmapper" % "modelmapper" % "2.3.7",
      "org.hamcrest" % "hamcrest-all" % "1.3",
      "org.mockito" % "mockito-core" % "3.3.3",
      "com.typesafe.play" %% "filters-helpers" % "2.8.1",
      "org.apache.kafka" % "kafka-clients" % "2.4.1",
      "io.searchbox" % "jest" % "6.3.1",
      "software.amazon.awssdk" % "dynamodb" % "2.10.91",
      "junit" % "junit" % "4.13" % Test

    ),
    PlayKeys.externalizeResources := false,
    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
//      "-Xms1G -Xmx1G -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=65 -XX:+UseCMSInitiatingOccupancyOnly -verbose:gc -Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps "
//      "-Xlint:unchecked",
//      "-Xlint:deprecation",
//      "-Werror"
    )
  )

//mainClass in assembly := Some("com.github.prorhap.coupon.play.app.Main")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
