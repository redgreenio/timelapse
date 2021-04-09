// Because we are okay with using lowercase names for object classes
@Suppress("ClassName")
object deps {
  object versions {
    const val mobius = "1.5.3"
    const val junit = "5.7.1"
    const val moshi = "1.12.0"
    const val java = "15"
    const val javaFx = "16"
  }

  const val jgit = "org.eclipse.jgit:org.eclipse.jgit:5.11.0.202103091610-r"
  const val caffeine = "com.github.ben-manes.caffeine:caffeine:3.0.1"
  const val eventBus = "org.greenrobot:eventbus:3.2.0"
  const val commonsText = "org.apache.commons:commons-text:1.9"
  const val sentry = "io.sentry:sentry:4.3.0"
  const val slf4jSimple = "org.slf4j:slf4j-simple:1.7.30"
  const val controlsFx = "org.controlsfx:controlsfx:11.1.0"

  object arrow {
    const val coreData = "io.arrow-kt:arrow-core-data:0.12.0"
  }

  object rxJava2 {
    const val runtime = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val javaFx = "io.reactivex.rxjava2:rxjavafx:2.2.2"
  }

  object rxJava3 {
    const val runtime = "io.reactivex.rxjava3:rxjava:3.0.11"
    const val bridge = "com.github.akarnokd:rxjava3-bridge:3.0.0"
  }

  object mobius {
    const val core = "com.spotify.mobius:mobius-core:${versions.mobius}"
    const val rx3 = "com.spotify.mobius:mobius-rx3:${versions.mobius}"
    const val extras = "com.spotify.mobius:mobius-extras:${versions.mobius}"
    const val test = "com.spotify.mobius:mobius-test:${versions.mobius}"
  }

  object humanize {
    const val slim = "com.github.mfornos:humanize-slim:1.2.2"
    const val jaxbApi = "javax.xml.bind:jaxb-api:2.4.0-b180830.0359"
  }

  object moshi {
    const val runtime = "com.squareup.moshi:moshi:${versions.moshi}"
    const val apt = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
  }

  object test {
    object junit {
      const val api = "org.junit.jupiter:junit-jupiter-api:${versions.junit}"
      const val params = "org.junit.jupiter:junit-jupiter-params:${versions.junit}"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:${versions.junit}"
    }

    object mockito {
      const val core = "org.mockito:mockito-core:3.8.0"
      const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    }

    const val truth = "com.google.truth:truth:1.1.2"
    const val approvalTests = "com.approvaltests:approvaltests:10.3.0"
  }
}
