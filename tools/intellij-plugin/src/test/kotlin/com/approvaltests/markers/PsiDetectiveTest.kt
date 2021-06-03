package com.approvaltests.markers

import com.google.common.truth.Truth.assertThat
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.LightIdeaTestCase
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtNamedFunction

class PsiDetectiveTest : LightIdeaTestCase() {
  companion object {
    private const val KT_FILE = "PsiTest.kt"
  }

  fun testAcquireKtNamedFunctionFromChildElement() {
    // given
    val source = """
      fun hello() {
        println("Hello, world!")
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val printlnIdentifier = psiFile.findElementAt(16)!!

    // when
    val ktNamedFunction = getNamedFunction(printlnIdentifier)

    // then
    assertThat(ktNamedFunction)
      .isInstanceOf(KtNamedFunction::class.java)
  }

  private fun psiFile(kotlinSource: String): PsiFile {
    return PsiFileFactory
      .getInstance(project)
      .createFileFromText(KT_FILE, KotlinLanguage.INSTANCE, kotlinSource)
  }
}
