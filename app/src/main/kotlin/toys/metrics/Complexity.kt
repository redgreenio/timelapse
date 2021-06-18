package toys.metrics

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

fun main() {
  val coreEnvironment = createKtCoreEnvironment()
  val psiFactory = KtPsiFactory(coreEnvironment.project, false)
  val ktFile = psiFactory.createFile(
    "X.kt",
    ClocCountVisitor::class.java.classLoader.getResourceAsStream("toys/complexity-kt.txt")!!.reader().readText()
  )

  val cognitiveComplexityVisitor = CognitiveComplexityVisitor()
  ktFile.accept(cognitiveComplexityVisitor)
  println("Cognitive complexity: ${cognitiveComplexityVisitor.complexity}")

  val cyclomaticComplexityVisitor = CyclomaticComplexityVisitor()
  ktFile.accept(cyclomaticComplexityVisitor)
  println("Cyclomatic complexity: ${cyclomaticComplexityVisitor.complexity}")
}

internal class ClocCountVisitor : KtTreeVisitorVoid() {
  internal var count = 0

  private fun increment(value: Int) {
    count += value
  }

  override fun visitComment(comment: PsiComment) {
    increment(comment.text.split('\n').size)
  }

  override fun visitDeclaration(dcl: KtDeclaration) {
    val text = dcl.docComment?.text
    if (text != null) {
      increment(text.split('\n').size)
    }
    super.visitDeclaration(dcl)
  }
}

class SlocVisitor : KtTreeVisitorVoid() {
  internal var count = 0

  override fun visitKtFile(file: KtFile) {
    val lines = file.text.split('\n')
    count = Sloc().count(lines)
  }

  private class Sloc {
    private val comments = arrayOf("//", "/*", "*/", "*")

    fun count(lines: List<String>): Int {
      return lines
        .map { it.trim() }
        .filter { trim -> trim.isNotEmpty() && !comments.any { trim.startsWith(it) } }
        .size
    }
  }
}

class CyclomaticComplexityVisitor : KtTreeVisitorVoid() {
  internal var complexity = 0

  override fun visitKtFile(file: KtFile) {
    complexity = CyclomaticComplexity.calculate(file) {
      ignoreSimpleWhenEntries = false
    }
  }
}

class CognitiveComplexityVisitor : KtTreeVisitorVoid() {
  internal var complexity = 0

  override fun visitKtFile(file: KtFile) {
    complexity = CognitiveComplexity.calculate(file)
  }
}

class CognitiveComplexity private constructor() : KtTreeVisitorVoid() {
  private var complexity: Int = 0

  override fun visitNamedFunction(function: KtNamedFunction) {
    val visitor = FunctionComplexity(function)
    visitor.visitNamedFunction(function)
    complexity += visitor.complexity
  }

  data class BinExprHolder(val expr: KtBinaryExpression, val op: IElementType, val isEnclosed: Boolean)

  @Suppress("detekt.TooManyFunctions") // visitor pattern
  inner class FunctionComplexity(
    private val givenFunction: KtNamedFunction
  ) : KtTreeVisitorVoid() {
    internal var complexity: Int = 0

    private var nesting: Int = 0

    private var topMostBinExpr: KtBinaryExpression? = null

    private fun addComplexity() {
      complexity += 1 + nesting
    }

    private inline fun nestAround(block: () -> Unit) {
      nesting++
      block()
      nesting--
    }

    private fun testJumpWithLabel(expression: KtExpressionWithLabel) {
      if (expression.labelQualifier != null) {
        complexity++
      }
    }

    private fun KtCallExpression.isRecursion(): Boolean {
      val args = lambdaArguments.size + valueArguments.size
      val isInsideSameScope = parent !is KtQualifiedExpression ||
        (parent as? KtQualifiedExpression)?.receiverExpression is KtThisExpression
      return isInsideSameScope &&
        getCallNameExpression()?.getReferencedName() == givenFunction.name &&
        args == givenFunction.valueParameters.size
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
      addComplexity()
      nestAround { super.visitWhenExpression(expression) }
    }

    override fun visitForExpression(expression: KtForExpression) {
      addComplexity()
      nestAround { super.visitForExpression(expression) }
    }

    override fun visitIfExpression(expression: KtIfExpression) {
      addComplexity()
      nestAround { super.visitIfExpression(expression) }
    }

    override fun visitBreakExpression(expression: KtBreakExpression) {
      testJumpWithLabel(expression)
      super.visitBreakExpression(expression)
    }

    override fun visitContinueExpression(expression: KtContinueExpression) {
      testJumpWithLabel(expression)
      super.visitContinueExpression(expression)
    }

    override fun visitReturnExpression(expression: KtReturnExpression) {
      testJumpWithLabel(expression)
      super.visitReturnExpression(expression)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
      if (function != givenFunction) {
        nestAround { super.visitNamedFunction(function) }
      } else {
        super.visitNamedFunction(function)
      }
    }

    override fun visitCatchSection(catchClause: KtCatchClause) {
      addComplexity()
      nestAround { super.visitCatchSection(catchClause) }
    }

    override fun visitWhileExpression(expression: KtWhileExpression) {
      addComplexity()
      nestAround { super.visitWhileExpression(expression) }
    }

    override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
      addComplexity()
      nestAround { super.visitDoWhileExpression(expression) }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
      if (expression.isRecursion()) {
        complexity++
      }
      super.visitCallExpression(expression)
    }

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
      nestAround { super.visitLambdaExpression(lambdaExpression) }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
      if (topMostBinExpr == null) {
        topMostBinExpr = expression
      }
      super.visitBinaryExpression(expression)
      if (topMostBinExpr == expression) {
        val nestedBinExprs = expression.collectDescendantsOfType<KtBinaryExpression>()
          .asSequence()
          .map { BinExprHolder(it, it.operationToken, it.parent is KtParenthesizedExpression) }
          .filter { it.op in logicalOps }
          .sortedBy { it.expr.operationReference.textRange.startOffset }
          .toList()
        calculateBinaryExprComplexity(nestedBinExprs)
        topMostBinExpr = null
      }
    }

    private fun calculateBinaryExprComplexity(usedExpr: List<BinExprHolder>) {
      var lastOp: IElementType? = null
      for (binExpr in usedExpr) {
        if (lastOp == null) {
          complexity++
        } else if (lastOp != binExpr.op || binExpr.isEnclosed) {
          complexity++
        }
        lastOp = binExpr.op
      }
    }
  }

  companion object {
    private val logicalOps = setOf(KtTokens.ANDAND, KtTokens.OROR)

    fun calculate(element: KtElement): Int {
      val visitor = CognitiveComplexity()
      element.accept(visitor)
      return visitor.complexity
    }
  }
}

fun createKtCoreEnvironment(
  configuration: CompilerConfiguration = CompilerConfiguration(),
  disposable: Disposable = Disposer.newDisposable()
): KotlinCoreEnvironment {
  // https://github.com/JetBrains/kotlin/commit/2568804eaa2c8f6b10b735777218c81af62919c1
  setIdeaIoUseFallback()
  configuration.put(
    CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
    PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
  )
  configuration.put(CommonConfigurationKeys.MODULE_NAME, "skrawberry")

  return KotlinCoreEnvironment.createForProduction(
    disposable,
    configuration,
    EnvironmentConfigFiles.JVM_CONFIG_FILES
  )
}

@Suppress("TooManyFunctions")
class CyclomaticComplexity(private val config: Config) : KtTreeVisitorVoid() {

  class Config(
    var ignoreSimpleWhenEntries: Boolean = false,
    var ignoreNestingFunctions: Boolean = false,
    var nestingFunctions: Set<String> = DEFAULT_NESTING_FUNCTIONS
  )

  var complexity: Int = 0
    private set

  override fun visitNamedFunction(function: KtNamedFunction) {
    if (!isInsideObjectLiteral(function)) {
      complexity++
      super.visitNamedFunction(function)
    }
  }

  private fun isInsideObjectLiteral(function: KtNamedFunction) =
    function.getStrictParentOfType<KtObjectLiteralExpression>() != null

  override fun visitBinaryExpression(expression: KtBinaryExpression) {
    if (expression.operationToken in CONDITIONALS) {
      complexity++
    }
    super.visitBinaryExpression(expression)
  }

  override fun visitContinueExpression(expression: KtContinueExpression) {
    complexity++
    super.visitContinueExpression(expression)
  }

  override fun visitBreakExpression(expression: KtBreakExpression) {
    complexity++
    super.visitBreakExpression(expression)
  }

  override fun visitIfExpression(expression: KtIfExpression) {
    complexity++
    super.visitIfExpression(expression)
  }

  override fun visitLoopExpression(loopExpression: KtLoopExpression) {
    complexity++
    super.visitLoopExpression(loopExpression)
  }

  override fun visitWhenExpression(expression: KtWhenExpression) {
    val entries = expression.extractEntries()
    complexity += if (config.ignoreSimpleWhenEntries && entries.count() == 0) 1 else entries.count()
    super.visitWhenExpression(expression)
  }

  private fun KtWhenExpression.extractEntries(): Sequence<KtWhenEntry> {
    val entries = entries.asSequence()
    return if (config.ignoreSimpleWhenEntries) entries.filter { it.expression is KtBlockExpression } else entries
  }

  override fun visitTryExpression(expression: KtTryExpression) {
    complexity += expression.catchClauses.size
    super.visitTryExpression(expression)
  }

  private fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
    in config.nestingFunctions -> true
    else -> false
  }

  override fun visitCallExpression(expression: KtCallExpression) {
    if (!config.ignoreNestingFunctions && expression.isUsedForNesting()) {
      val lambdaExpression = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
      lambdaExpression?.bodyExpression?.let {
        complexity++
      }
    }
    super.visitCallExpression(expression)
  }

  companion object {

    val CONDITIONALS = setOf(KtTokens.ELVIS, KtTokens.ANDAND, KtTokens.OROR)
    val DEFAULT_NESTING_FUNCTIONS = setOf(
      "run",
      "let",
      "apply",
      "with",
      "also",
      "use",
      "forEach",
      "isNotNull",
      "ifNull"
    )

    fun calculate(node: KtElement, configure: (Config.() -> Unit)? = null): Int {
      val config = Config()
      configure?.invoke(config)
      val visitor = CyclomaticComplexity(config)
      node.accept(visitor)
      return visitor.complexity
    }
  }
}
