// Because we are okay with using lowercase names for object classes
@Suppress("ClassName")
object deps {
  object versions {
    const val java = "15"
    const val javaFx = "15"
  }

  const val jgit = "org.eclipse.jgit:org.eclipse.jgit:5.11.1.202105131744-r"
  const val caffeine = "com.github.ben-manes.caffeine:caffeine:3.0.2"
  const val eventBus = "org.greenrobot:eventbus:3.2.0"
  const val commonsText = "org.apache.commons:commons-text:1.9"
  const val sentry = "io.sentry:sentry:4.3.0"
  const val slf4jSimple = "org.slf4j:slf4j-simple:1.7.30"
  const val controlsFx = "org.controlsfx:controlsfx:11.1.0"
  const val diff4j = "com.cloudbees:diff4j:1.3" /* Repackaged the code from NetBeans */
  const val picocli = "info.picocli:picocli:4.6.1"
  const val jansi = "org.fusesource.jansi:jansi:2.3.2"

  object arrow {
    const val coreData = "io.arrow-kt:arrow-core-data:0.12.1"
  }

  object rxJava2 {
    const val runtime = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val javaFx = "io.reactivex.rxjava2:rxjavafx:2.2.2"
  }

  object rxJava3 {
    const val runtime = "io.reactivex.rxjava3:rxjava:3.0.12"
    const val bridge = "com.github.akarnokd:rxjava3-bridge:3.0.0"
  }

  object mobius {
    private const val version = "1.5.3"

    const val core = "com.spotify.mobius:mobius-core:$version"
    const val rx3 = "com.spotify.mobius:mobius-rx3:$version"
    const val extras = "com.spotify.mobius:mobius-extras:$version"
    const val test = "com.spotify.mobius:mobius-test:$version"
  }

  object humanize {
    const val slim = "com.github.mfornos:humanize-slim:1.2.2"
    const val jaxbApi = "javax.xml.bind:jaxb-api:2.4.0-b180830.0359"
  }

  object moshi {
    private const val version = "1.12.0"

    const val runtime = "com.squareup.moshi:moshi:$version"
    const val apt = "com.squareup.moshi:moshi-kotlin-codegen:$version"
  }

  object log4j {
    private const val version = "2.14.1"

    const val api = "org.apache.logging.log4j:log4j-api:$version"
    const val core = "org.apache.logging.log4j:log4j-core:$version"
  }

  object antlr {
    private const val version = "4.9.2"

    const val core = "org.antlr:antlr4:$version"
    const val runtime = "org.antlr:antlr4-runtime:$version"
  }

  object test {
    object junit {
      private const val version = "5.7.2"

      const val api = "org.junit.jupiter:junit-jupiter-api:$version"
      const val params = "org.junit.jupiter:junit-jupiter-params:$version"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
    }

    object mockito {
      const val core = "org.mockito:mockito-core:3.10.0"
      const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    }

    const val truth = "com.google.truth:truth:1.1.2"
    const val approvalTests = "com.approvaltests:approvaltests:11.2.3"
  }
}
