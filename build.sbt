name := "scala3-pekko-project-bank-core"

version := "0.1.0-SNAPSHOT"

// Используем самую актуальную и стабильную версию Scala 3
scalaVersion := "3.3.4"

// Строгие настройки компилятора вынесены отдельно
scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

// Версии библиотек
val pekkoVersion     = "1.1.2"
val pekkoHttpVersion = "1.1.0"
val logbackVersion   = "1.5.6"
val scalaTestVersion = "3.2.19"

// Зависимости подключаем напрямую к корневому проекту
libraryDependencies ++= Seq(
  // 1. Ядро Apache Pekko
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,

  // 2. Pekko HTTP
  "org.apache.pekko" %% "pekko-http"            % pekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,

  // 3. Логирование
  "org.apache.pekko" %% "pekko-slf4j"     % pekkoVersion,
  "ch.qos.logback"    % "logback-classic" % logbackVersion,

  // 4. Тестирование
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion     % Test,
  "org.scalatest"    %% "scalatest"                 % scalaTestVersion % Test
)

