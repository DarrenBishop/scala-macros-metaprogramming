import sbt.*

trait DependenciesBase extends Support.Functions {

  object Compiler {
    object versions {
      lazy val macroParadise = "2.1.1"
      lazy val kindProjector = "0.13.3"
    }

    lazy val macroParadise_? = addCompilerPluginBefore("3")("org.scalamacros" % "paradise" % versions.macroParadise cross CrossVersion.full)
    lazy val kindProjector_?    = addCompilerPluginBefore("3")("org.typelevel" % "kind-projector" % versions.kindProjector cross CrossVersion.full)
  }

  object Typelevel {

    object versions {
      lazy val cats = "2.13.0"
      lazy val refined = "0.11.3"
    }

    lazy val cats = "org.typelevel" %% "cats-core" % versions.cats
    lazy val alleycats = "org.typelevel" %% "alleycats-core" % versions.cats
    lazy val mouse = "org.typelevel" %% "mouse" % "1.4.0"
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.7.0"
    lazy val catsTagless = "org.typelevel" %% "cats-tagless-core" % "0.16.5"
    lazy val simulacrum = "org.typelevel" %% "simulacrum" % "1.0.2-SNAPSHOT"
    lazy val refined = "eu.timepit" %% "refined" % versions.refined
  }

  object Config {

    object versions {
      lazy val pureconfig = "0.17.9"
    }

    lazy val core = "com.github.pureconfig" %% "pureconfig-core" % versions.pureconfig
    lazy val cats = "com.github.pureconfig" %% "pureconfig-cats" % versions.pureconfig
    lazy val generic = "com.github.pureconfig" %% "pureconfig-generic" % versions.pureconfig
    lazy val refined = "eu.timepit" %% "refined-pureconfig" % Typelevel.versions.refined
  }

  object Logging {

    object versions {
      lazy val slf4J = "2.0.17"
      lazy val logback = "1.5.18"
      lazy val scalaLogging = "3.9.5"
    }

    lazy val slf4j = "org.slf4j" % "slf4j-api" % versions.slf4J
    lazy val slf4j4Log4J = "org.slf4j" % "log4j-over-slf4j" % versions.slf4J
    lazy val logback = "ch.qos.logback" % "logback-classic" % versions.logback
    lazy val logbackCore = "ch.qos.logback" % "logback-core" % versions.logback
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % versions.scalaLogging
  }

  object Metrics {

    object versions
  }

  object Testing {

    object versions {

      lazy val scalatest = "3.2.19"
      lazy val mockito = "1.17.45"
    }

    lazy val scalatest = "org.scalatest" %% "scalatest" % versions.scalatest % Test
    lazy val scalaCheck = s"org.scalatestplus" %% "scalacheck-1-18" % s"${versions.scalatest}.0" % Test
    lazy val scalaCheckRefined = "eu.timepit" %% "refined-scalacheck" % Typelevel.versions.refined
    lazy val scalaCheckEffect = "org.typelevel" %% "scalacheck-effect" % "1.0.4" % Test
    lazy val catsScalaCheck = "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2" % Test

    lazy val mockitoScala = "org.mockito" %% "mockito-scala" % versions.mockito % Test
    lazy val mockitoScalatest = "org.mockito" %% "mockito-scala-scalatest" % versions.mockito % Test
  }

  object Data {
    lazy val postgres = "org.postgresql" % "postgresql" % "42.7.10"
  }

  object Versions {
    val adept = "2.51.0"

    val cats           = "2.1.1"
    val catsEffect     = "2.1.4"
    val circe          = "0.13.0"
    val fs2            = "2.4.2"
    val refined        = "0.9.14"
    val geotrellis     = "3.3.0"
    val pureconfig     = "0.13.0"
    val avro           = "1.10.0"
    val avroSerializer = "5.5.1"
    val parquetAvro    = "1.9.0"
    val slf4J          = "1.7.30"
    val scalaLogging   = "3.9.2"
    val logback        = "1.2.3"
    val hadoop         = "2.8.0"
    val http4s         = "0.21.17"
    val s3Mock         = "0.2.6"
    val mockserver     = "5.11.1"

    object kafka {
      // This is the version of the Kafka client libs we use in the core app.
      // Note: The Kafka brokers are running Kafka 1.1.0.
      val core = "2.5.0"

      // This matches the version of Kafka Connect running in staging.
      val connect = "2.2.2"

      // Confluent Platform 5.2.x is based on Kafka (and Kafka Connect) 2.2.x
      // so really we should use a 5.2.x dependency here, but...
      //
      // - Parquet support was added in 5.4.0, so we need 5.4.x or newer to
      //   avoid breaking our e2e tests (although it's not clear whether we will
      //   ever need Parquet in production)
      //
      // - Confluent's libraries don't specify a version for their dependencies
      //   in their pom.xml, so transitive dependencies will default to the latest
      //   released version (that's 5.5.1 as of right now)
      //
      // - Confluent have a habit of making huge breaking changes in minor versions
      //   of their libraries, leading to NoClassDefFoundErrors etc at runtime.
      //   e.g. between v5.4.2 and v5.5.0 of kafka-avro-serializer, they moved
      //   io.confluent.kafka.serializers.AvroSchemaUtils to
      //   io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils.
      //
      // Conclusion: set these to the latest released version, and try to keep them
      // up to date or else we risk blowing up at runtime the next time Confluent
      // release a breaking change.
      val connectS3            = "5.5.1"
      val connectAvroConverter = "5.5.1"
    }

    object azure {
      val blobStorage = "12.9.0"
    }

    object kamon {
      val core       = "2.1.9"
      val log        = "2.1.99"
      val cloudwatch = "1.1.5"
      val agent      = "1.0.5"
    }

    val scalatest               = "3.2.0"
    val scalacheck              = "1.14.3"
    val scalatestplusScalacheck = "3.2.0.0"
    val mockito                 = "1.16.25"
    val awsS3Sdk                = "1.11.820"
    val localStack              = "0.2.1"
    val jts                     = "1.13"
    val okhttp                  = "4.8.0"
  }

  object Libraries {

    //object wejo { // TODO: remove from history
    //  val cdmSchema = "com.wejo" % "adept-cdm-schema" % Versions.adept // TODO: remove from history
    //  val adeptCore = "com.wejo" %% "adept-core"      % Versions.adept // TODO: remove from history
    //}

    object kafka {
      val streams = "org.apache.kafka" %% "kafka-streams-scala" % Versions.kafka.core
      val core    = "org.apache.kafka" %% "kafka"               % Versions.kafka.core
      val clients = "org.apache.kafka" % "kafka-clients"        % Versions.kafka.core

      object connect {
        val api           = "org.apache.kafka" % "connect-api"                  % Versions.kafka.connect
        val json          = "org.apache.kafka" % "connect-json"                 % Versions.kafka.connect
        val s3            = "io.confluent"     % "kafka-connect-s3"             % Versions.kafka.connectS3
        val avroConverter = "io.confluent"     % "kafka-connect-avro-converter" % Versions.kafka.connectAvroConverter
      }

      val embeddedCore = "io.github.embeddedkafka" %% "embedded-kafka" % Versions.kafka.core
      val embeddedSchemaRegistry =
        "io.github.embeddedkafka" %% "embedded-kafka-schema-registry" % Versions.avroSerializer excludeAll ExclusionRule(
          organization = "org.ow2.asm"
        )
    }

    object azure {
      val blobStorage = "com.azure" % "azure-storage-blob" % Versions.azure.blobStorage
    }

    object logging {
      val slf4j        = "org.slf4j"                  % "slf4j-api"        % Versions.slf4J
      val slf4j4Log4J  = "org.slf4j"                  % "log4j-over-slf4j" % Versions.slf4J
      val logback      = "ch.qos.logback"             % "logback-classic"  % Versions.logback
      val logbackCore  = "ch.qos.logback"             % "logback-core"     % Versions.logback
      val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"   % Versions.scalaLogging
    }

    object hadoop {
      val common = "org.apache.hadoop" % "hadoop-common" % Versions.hadoop
      val aws    = "org.apache.hadoop" % "hadoop-aws"    % Versions.hadoop
      val client = "org.apache.hadoop" % "hadoop-client" % Versions.hadoop
    }

    object kamon {
      val core       = "io.kamon"               %% "kamon-core"           % Versions.kamon.core
      val system     = "io.kamon"               %% "kamon-system-metrics" % Versions.kamon.core exclude ("org.slf4j", "slf4j-api")
      val statusPage = "io.kamon"               %% "kamon-status-page"    % Versions.kamon.core
      val bundle     = "io.kamon"               %% "kamon-bundle"         % Versions.kamon.core
      val cloudwatch = "com.github.alonsodomin" %% "kamon-cloudwatch"     % Versions.kamon.cloudwatch
      val testkit    = "io.kamon"               %% "kamon-testkit"        % Versions.kamon.core
      val agent      = "io.kamon"               % "kanela-agent"          % Versions.kamon.agent
    }

    object http4s {
      val core        = "org.http4s" %% "http4s-core"     % Versions.http4s
      val circe       = "org.http4s" %% "http4s-circe"        % Versions.http4s
      val blazeServer = "org.http4s" %% "http4s-blaze-server" % Versions.http4s
      val blazeClient = "org.http4s" %% "http4s-blaze-client" % Versions.http4s
      val dsl         = "org.http4s" %% "http4s-dsl"          % Versions.http4s
    }

    object circe {
      val core          = "io.circe" %% "circe-core"           % Versions.circe
      val parser        = "io.circe" %% "circe-parser"         % Versions.circe
      val generic       = "io.circe" %% "circe-generic"        % Versions.circe
      val genericExtras = "io.circe" %% "circe-generic-extras" % Versions.circe
    }

    val catsCore   = "org.typelevel" %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

    val fs2 = "co.fs2" %% "fs2-core" % Versions.fs2

    val pureconfigCore = "com.github.pureconfig" %% "pureconfig"           % Versions.pureconfig
    val avroSerializer = "io.confluent"          % "kafka-avro-serializer" % Versions.avroSerializer
    val avroCore       = "org.apache.avro"       % "avro"                  % Versions.avro
    val parquetAvro    = "org.apache.parquet"    % "parquet-avro"          % Versions.parquetAvro

    val scalatestCore           = "org.scalatest"     %% "scalatest"       % Versions.scalatest
    val scalacheckCore          = "org.scalacheck"    %% "scalacheck"      % Versions.scalacheck
    val scalatestplusScalacheck = "org.scalatestplus" %% "scalacheck-1-14" % Versions.scalatestplusScalacheck
    val s3Mock                  = "io.findify"        %% "s3mock"          % Versions.s3Mock
    val mockserver              = "org.mock-server"   % "mockserver-netty" % Versions.mockserver

    val awsS3Sdk   = "com.amazonaws"        % "aws-java-sdk-s3"  % Versions.awsS3Sdk
    val localStack = "cloud.localstack"     % "localstack-utils" % Versions.localStack
    val jts        = "com.vividsolutions"   % "jts"              % Versions.jts
    val okhttp     = "com.squareup.okhttp3" % "okhttp"           % Versions.okhttp

    object mockito {
      val core      = "org.mockito" %% "mockito-scala"           % Versions.mockito
      val scalatest = "org.mockito" %% "mockito-scala-scalatest" % Versions.mockito
    }
  }

  val coreDeps = Seq(
    Libraries.kamon.bundle,
    Libraries.kamon.cloudwatch,
    Libraries.catsCore,
    Libraries.catsEffect,
    Libraries.circe.core,
    Libraries.circe.parser,
    Libraries.circe.generic,
    Libraries.circe.genericExtras,
    Libraries.fs2,
    Libraries.jts,
    Libraries.kafka.core,
    Libraries.kafka.clients,
    Libraries.kafka.streams,
    Libraries.pureconfigCore,
    Libraries.avroCore,
    Libraries.avroSerializer,
    //Libraries.wejo.cdmSchema, // TODO: remove from history
    //Libraries.wejo.adeptCore, // TODO: remove from history
    Libraries.logging.slf4j,
    Libraries.logging.slf4j4Log4J,
    Libraries.logging.logback,
    Libraries.logging.scalaLogging,
    Libraries.http4s.core,
    Libraries.http4s.blazeServer,
    Libraries.http4s.dsl,
    Libraries.http4s.blazeClient,
    Libraries.http4s.circe
  )

  val commonConnectorDeps = Seq(
    Libraries.logging.slf4j,
    Libraries.logging.slf4j4Log4J,
    Libraries.logging.scalaLogging,
    Libraries.catsCore,
    Libraries.kamon.core,
    Libraries.kamon.system,
    Libraries.kamon.cloudwatch,
    Libraries.kafka.connect.avroConverter
  )

  val providedConnectorDeps = Seq(
    Libraries.logging.logback    % Provided,
    Libraries.kafka.connect.api  % Provided,
    Libraries.kafka.connect.json % Provided
  )

  val testConnectorDeps = Seq(
    Libraries.kafka.connect.json % Test
  )

  val s3SinkConnectorDeps = Seq(
    Libraries.kafka.connect.s3
  )

  val azureBsSinkConnectorDeps = Seq(
    Libraries.azure.blobStorage
  )

  val httpSinkConnectorDeps = Seq(
    Libraries.okhttp,
    Libraries.circe.core,
    Libraries.circe.generic,
    Libraries.circe.parser,
    Libraries.mockserver % Test
  )

  val testDeps = Seq(
    Libraries.scalatestCore                % Test,
    Libraries.scalacheckCore               % Test,
    Libraries.scalatestplusScalacheck      % Test,
    Libraries.kafka.embeddedCore           % Test,
    Libraries.kafka.embeddedSchemaRegistry % Test,
    Libraries.mockito.core                 % Test,
    Libraries.mockito.scalatest            % Test,
    Libraries.kamon.testkit                % Test,
    Libraries.s3Mock                       % Test
  )

  val e2eDeps = Seq(
    Libraries.parquetAvro   % Test,
    Libraries.awsS3Sdk      % Test,
    Libraries.localStack    % Test,
    Libraries.hadoop.aws    % Test,
    Libraries.hadoop.common % Test,
    Libraries.hadoop.client % Test,
    Libraries.localStack    % Test,
    Libraries.okhttp        % Test,
    Libraries.circe.core    % Test,
    Libraries.circe.parser  % Test
  )

  object Agents {
    val kamon = Libraries.kamon.agent
  }

  object Excludes {
    val core    = Seq("org.slf4j"           % "slf4j-log4j12")
    val connect = core ++ Seq("org.pentaho" % "pentaho-aggdesigner-algorithm")
  }
}

object Dependencies extends DependenciesBase
