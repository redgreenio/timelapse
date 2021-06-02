import com.google.common.truth.Truth.assertThat
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.LightIdeaTestCase
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile

class CanaryPsiTest : LightIdeaTestCase() {
  fun testCreatePsiFromPlainText() {
    // given
    val fileName = "Hello.kt"
    val source = """
      fun hello() {
        println("Hello!")
      }
    """.trimIndent()

    // when
    val psiFile = PsiFileFactory
      .getInstance(project)
      .createFileFromText(fileName, KotlinLanguage.INSTANCE, source)

    // then
    assertThat(psiFile)
      .isInstanceOf(KtFile::class.java)
  }
}
