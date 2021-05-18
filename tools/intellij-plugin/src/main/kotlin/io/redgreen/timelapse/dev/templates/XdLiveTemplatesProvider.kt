package io.redgreen.timelapse.dev.templates

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider

class XdLiveTemplatesProvider : DefaultLiveTemplatesProvider {
  override fun getDefaultLiveTemplateFiles(): Array<String> =
    arrayOf("live-templates/xd.xml")

  override fun getHiddenLiveTemplateFiles(): Array<String> =
    emptyArray()
}
