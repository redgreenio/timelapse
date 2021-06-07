package com.approvaltests.markers

import com.approvaltests.model.FunctionCoordinates
import com.google.common.truth.Truth.assertThat
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightIdeaTestCase
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
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

  fun testGetFunKeywordReturnsElement() {
    // given
    val source = """
      private const val GREETING = "Good, morning!"

      fun greet() {
        println(GREETING)
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val greetKtNamedFunction = getKtNamedFunction(psiFile.findElementAt(63)!!)!!

    // when
    val funKeyword = getFunKeyword(greetKtNamedFunction)

    // then
    assertThat(funKeyword.textRange)
      .isEqualTo(TextRange(47, 50))
  }

  fun testIsApprovalsVerifyCallReturnsFalse() {
    val source = """
      fun hello() {
        System.out.println("Hello, world!")
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val dotQualifiedExpression = PsiTreeUtil.findChildOfType(psiFile, KtDotQualifiedExpression::class.java)!!

    // when & then
    assertThat(isApprovalsVerifyCall(dotQualifiedExpression))
      .isFalse()
  }

  fun testIsApprovalsVerifyCallReturnsTrue() {
    val source = """
      fun approvalsTest() {
        Approvals.verify("Hello, world!")
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val dotQualifiedExpression = PsiTreeUtil.findChildOfType(psiFile, KtDotQualifiedExpression::class.java)!!

    // when & then
    assertThat(isApprovalsVerifyCall(dotQualifiedExpression))
      .isTrue()
  }

  fun testIsApprovalsVerifyXCallReturnsTrue() {
    val source = """
      fun approvalsTest() {
        Approvals.verifyHtml("<html>Hello, world!</html>")
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val dotQualifiedExpression = PsiTreeUtil.findChildOfType(psiFile, KtDotQualifiedExpression::class.java)!!

    // when & then
    assertThat(isApprovalsVerifyCall(dotQualifiedExpression))
      .isTrue()
  }

  fun testHasApprovalsVerifyXCallWithMultipleDotExpressionsReturnsTrue() {
    val source = """
      fun approvalsTest() {
        val dog = Dog("Oreo")
        dark.bark()
        Approvals.verifyHtml(dog.toHtml())
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val dotQualifiedExpression = PsiTreeUtil.findChildrenOfType(psiFile, KtDotQualifiedExpression::class.java)!!

    // when & then
    assertThat(hasApprovalsVerifyCall(dotQualifiedExpression))
      .isTrue()
  }

  fun testFunctionCoordinatesClassFunction() {
    // given
    val source = """
      class Math {
        fun add(a: Int, b: Int): Int = a + b
      }
    """.trimIndent()
    val psiFile = psiFile(source)
    val addNamedFunction = getKtNamedFunction(psiFile.findElementAt(16)!!)!!

    // when & then
    assertThat(getFunctionCoordinates(addNamedFunction))
      .isEqualTo(FunctionCoordinates.from("add", "Math"))
  }

  private fun psiFile(kotlinSource: String): PsiFile {
    return PsiFileFactory
      .getInstance(project)
      .createFileFromText(KT_FILE, KotlinLanguage.INSTANCE, kotlinSource)
  }
}
