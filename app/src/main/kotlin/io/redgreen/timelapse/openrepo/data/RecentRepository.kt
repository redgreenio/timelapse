package io.redgreen.timelapse.openrepo.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentRepository(
  @Json(name = "path") val path: String
)
