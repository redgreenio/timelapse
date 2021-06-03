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

  fun testGetKtNamedFunctionReturnsInstanceFromChildElement() {
    // given
    val source = """
      fun hello() {
        println("Hello, world!")
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val printlnIdentifier = psiFile.findElementAt(16)!!

    // when
    val ktNamedFunction = getKtNamedFunction(printlnIdentifier)

    // then
    assertThat(ktNamedFunction)
      .isInstanceOf(KtNamedFunction::class.java)
  }

  fun testGetKtNamedFunctionReturnsNullIfElementIsNotChild() {
    // given
    val source = """
      private const val GREETING = "Good, morning!"

      fun greet() {
        println(GREETING)
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val greetingConstIdentifier = psiFile.findElementAt(18)!!

    // when
    val ktNamedFunction = getKtNamedFunction(greetingConstIdentifier)

    // then
    assertThat(ktNamedFunction)
      .isNull()
  }

  private fun psiFile(kotlinSource: String): PsiFile {
    return PsiFileFactory
      .getInstance(project)
      .createFileFromText(KT_FILE, KotlinLanguage.INSTANCE, kotlinSource)
  }
}
