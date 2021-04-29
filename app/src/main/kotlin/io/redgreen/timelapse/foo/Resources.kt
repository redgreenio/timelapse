package io.redgreen.timelapse.foo

import java.net.URL

fun Any.readResourceText(resourcePath: String): String {
  val resourceUrl = getResourceUrl(resourcePath)
  checkNotNull(resourceUrl) { "Unable to find resource at: $resourcePath" }

  return resourceUrl.readText()
}

fun Any.getResourceUrl(resourcePath: String): URL? {
  return this::class
    .java
    .getResource(resourcePath)
}
