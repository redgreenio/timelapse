package io.redgreen.language.kotlin

import KotlinParser
import KotlinParserBaseVisitor

@SuppressWarnings("TooManyFunctions", "LargeClass")
class KotlinLanguageElementVisitor : KotlinParserBaseVisitor<LanguageElement>() {
  companion object {
    private const val DEBUG = false
  }

  private val mutableFunctions = mutableListOf<Function>()

  val functions: List<Function>
    get() = mutableFunctions.toList()

  override fun visitKotlinFile(ctx: KotlinParser.KotlinFileContext?): LanguageElement? {
    debug("visitKotlinFile")
    return super.visitKotlinFile(ctx)
  }

  override fun visitScript(ctx: KotlinParser.ScriptContext?): LanguageElement? {
    debug("visitScript")
    return super.visitScript(ctx)
  }

  override fun visitFileAnnotation(ctx: KotlinParser.FileAnnotationContext?): LanguageElement? {
    debug("visitFileAnnotation")
    return super.visitFileAnnotation(ctx)
  }

  override fun visitPackageHeader(ctx: KotlinParser.PackageHeaderContext?): LanguageElement? {
    debug("visitPackageHeader")
    return super.visitPackageHeader(ctx)
  }

  override fun visitImportList(ctx: KotlinParser.ImportListContext?): LanguageElement? {
    debug("visitImportList")
    return super.visitImportList(ctx)
  }

  override fun visitImportHeader(ctx: KotlinParser.ImportHeaderContext?): LanguageElement? {
    debug("visitImportHeader")
    return super.visitImportHeader(ctx)
  }

  override fun visitImportAlias(ctx: KotlinParser.ImportAliasContext?): LanguageElement? {
    debug("visitImportAlias")
    return super.visitImportAlias(ctx)
  }

  override fun visitTopLevelObject(ctx: KotlinParser.TopLevelObjectContext?): LanguageElement? {
    debug("visitTopLevelObject")
    return super.visitTopLevelObject(ctx)
  }

  override fun visitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?): LanguageElement? {
    debug("visitClassDeclaration")
    return super.visitClassDeclaration(ctx)
  }

  override fun visitPrimaryConstructor(ctx: KotlinParser.PrimaryConstructorContext?): LanguageElement? {
    debug("visitPrimaryConstructor")
    return super.visitPrimaryConstructor(ctx)
  }

  override fun visitClassParameters(ctx: KotlinParser.ClassParametersContext?): LanguageElement? {
    debug("visitClassParameters")
    return super.visitClassParameters(ctx)
  }

  override fun visitClassParameter(ctx: KotlinParser.ClassParameterContext?): LanguageElement? {
    debug("visitClassParameter")
    return super.visitClassParameter(ctx)
  }

  override fun visitDelegationSpecifiers(ctx: KotlinParser.DelegationSpecifiersContext?): LanguageElement? {
    debug("visitDelegationSpecifiers")
    return super.visitDelegationSpecifiers(ctx)
  }

  override fun visitAnnotatedDelegationSpecifier(
    ctx: KotlinParser.AnnotatedDelegationSpecifierContext
  ): LanguageElement? {
    return super.visitAnnotatedDelegationSpecifier(ctx)
  }

  override fun visitDelegationSpecifier(ctx: KotlinParser.DelegationSpecifierContext?): LanguageElement? {
    debug("visitDelegationSpecifier")
    return super.visitDelegationSpecifier(ctx)
  }

  override fun visitConstructorInvocation(ctx: KotlinParser.ConstructorInvocationContext?): LanguageElement? {
    debug("visitConstructorInvocation")
    return super.visitConstructorInvocation(ctx)
  }

  override fun visitExplicitDelegation(ctx: KotlinParser.ExplicitDelegationContext?): LanguageElement? {
    debug("visitExplicitDelegation")
    return super.visitExplicitDelegation(ctx)
  }

  override fun visitClassBody(ctx: KotlinParser.ClassBodyContext?): LanguageElement? {
    debug("visitClassBody")
    return super.visitClassBody(ctx)
  }

  override fun visitClassMemberDeclarations(ctx: KotlinParser.ClassMemberDeclarationsContext?): LanguageElement? {
    debug("visitClassMemberDeclarations")
    return super.visitClassMemberDeclarations(ctx)
  }

  override fun visitClassMemberDeclaration(ctx: KotlinParser.ClassMemberDeclarationContext?): LanguageElement? {
    debug("visitClassMemberDeclaration")
    return super.visitClassMemberDeclaration(ctx)
  }

  override fun visitAnonymousInitializer(ctx: KotlinParser.AnonymousInitializerContext?): LanguageElement? {
    debug("visitAnonymousInitializer")
    return super.visitAnonymousInitializer(ctx)
  }

  override fun visitSecondaryConstructor(ctx: KotlinParser.SecondaryConstructorContext?): LanguageElement? {
    debug("visitSecondaryConstructor")
    return super.visitSecondaryConstructor(ctx)
  }

  override fun visitConstructorDelegationCall(ctx: KotlinParser.ConstructorDelegationCallContext?): LanguageElement? {
    debug("visitConstructorDelegationCall")
    return super.visitConstructorDelegationCall(ctx)
  }

  override fun visitEnumClassBody(ctx: KotlinParser.EnumClassBodyContext?): LanguageElement? {
    debug("visitEnumClassBody")
    return super.visitEnumClassBody(ctx)
  }

  override fun visitEnumEntries(ctx: KotlinParser.EnumEntriesContext?): LanguageElement? {
    debug("visitEnumEntries")
    return super.visitEnumEntries(ctx)
  }

  override fun visitEnumEntry(ctx: KotlinParser.EnumEntryContext?): LanguageElement? {
    debug("visitEnumEntry")
    return super.visitEnumEntry(ctx)
  }

  override fun visitFunctionValueParameters(ctx: KotlinParser.FunctionValueParametersContext?): LanguageElement? {
    debug("visitFunctionValueParameters")
    return super.visitFunctionValueParameters(ctx)
  }

  override fun visitFunctionValueParameter(ctx: KotlinParser.FunctionValueParameterContext?): LanguageElement? {
    debug("visitFunctionValueParameter")
    return super.visitFunctionValueParameter(ctx)
  }

  override fun visitParameter(ctx: KotlinParser.ParameterContext?): LanguageElement? {
    debug("visitParameter")
    return super.visitParameter(ctx)
  }

  override fun visitSetterParameter(ctx: KotlinParser.SetterParameterContext?): LanguageElement? {
    debug("visitSetterParameter")
    return super.visitSetterParameter(ctx)
  }

  override fun visitFunctionBody(ctx: KotlinParser.FunctionBodyContext?): LanguageElement? {
    debug("visitFunctionBody")
    return super.visitFunctionBody(ctx)
  }

  override fun visitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?): LanguageElement? {
    debug("visitObjectDeclaration")
    return super.visitObjectDeclaration(ctx)
  }

  override fun visitCompanionObject(ctx: KotlinParser.CompanionObjectContext?): LanguageElement? {
    debug("visitCompanionObject")
    return super.visitCompanionObject(ctx)
  }

  override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext?): LanguageElement? {
    debug("visitPropertyDeclaration")
    return super.visitPropertyDeclaration(ctx)
  }

  override fun visitMultiVariableDeclaration(ctx: KotlinParser.MultiVariableDeclarationContext?): LanguageElement? {
    debug("visitMultiVariableDeclaration")
    return super.visitMultiVariableDeclaration(ctx)
  }

  override fun visitVariableDeclaration(ctx: KotlinParser.VariableDeclarationContext?): LanguageElement? {
    debug("visitVariableDeclaration")
    return super.visitVariableDeclaration(ctx)
  }

  override fun visitPropertyDelegate(ctx: KotlinParser.PropertyDelegateContext?): LanguageElement? {
    debug("visitPropertyDelegate")
    return super.visitPropertyDelegate(ctx)
  }

  override fun visitGetter(ctx: KotlinParser.GetterContext?): LanguageElement? {
    debug("visitGetter")
    return super.visitGetter(ctx)
  }

  override fun visitSetter(ctx: KotlinParser.SetterContext?): LanguageElement? {
    debug("visitSetter")
    return super.visitSetter(ctx)
  }

  override fun visitTypeAlias(ctx: KotlinParser.TypeAliasContext?): LanguageElement? {
    debug("visitTypeAlias")
    return super.visitTypeAlias(ctx)
  }

  override fun visitTypeParameters(ctx: KotlinParser.TypeParametersContext?): LanguageElement? {
    debug("visitTypeParameters")
    return super.visitTypeParameters(ctx)
  }

  override fun visitTypeParameter(ctx: KotlinParser.TypeParameterContext?): LanguageElement? {
    debug("visitTypeParameter")
    return super.visitTypeParameter(ctx)
  }

  override fun visitTypeParameterModifiers(ctx: KotlinParser.TypeParameterModifiersContext?): LanguageElement? {
    debug("visitTypeParameterModifiers")
    return super.visitTypeParameterModifiers(ctx)
  }

  override fun visitTypeParameterModifier(ctx: KotlinParser.TypeParameterModifierContext?): LanguageElement? {
    debug("visitTypeParameterModifier")
    return super.visitTypeParameterModifier(ctx)
  }

  override fun visitType_(ctx: KotlinParser.Type_Context?): LanguageElement? {
    debug("visitType_")
    return super.visitType_(ctx)
  }

  override fun visitTypeModifiers(ctx: KotlinParser.TypeModifiersContext?): LanguageElement? {
    debug("visitTypeModifiers")
    return super.visitTypeModifiers(ctx)
  }

  override fun visitTypeModifier(ctx: KotlinParser.TypeModifierContext?): LanguageElement? {
    debug("visitTypeModifier")
    return super.visitTypeModifier(ctx)
  }

  override fun visitParenthesizedType(ctx: KotlinParser.ParenthesizedTypeContext?): LanguageElement? {
    debug("visitParenthesizedType")
    return super.visitParenthesizedType(ctx)
  }

  override fun visitNullableType(ctx: KotlinParser.NullableTypeContext?): LanguageElement? {
    debug("visitNullableType")
    return super.visitNullableType(ctx)
  }

  override fun visitTypeReference(ctx: KotlinParser.TypeReferenceContext?): LanguageElement? {
    debug("visitTypeReference")
    return super.visitTypeReference(ctx)
  }

  override fun visitFunctionType(ctx: KotlinParser.FunctionTypeContext?): LanguageElement? {
    debug("visitFunctionType")
    return super.visitFunctionType(ctx)
  }

  override fun visitReceiverType(ctx: KotlinParser.ReceiverTypeContext?): LanguageElement? {
    debug("visitReceiverType")
    return super.visitReceiverType(ctx)
  }

  override fun visitUserType(ctx: KotlinParser.UserTypeContext?): LanguageElement? {
    debug("visitUserType")
    return super.visitUserType(ctx)
  }

  override fun visitParenthesizedUserType(ctx: KotlinParser.ParenthesizedUserTypeContext?): LanguageElement? {
    debug("visitParenthesizedUserType")
    return super.visitParenthesizedUserType(ctx)
  }

  override fun visitSimpleUserType(ctx: KotlinParser.SimpleUserTypeContext?): LanguageElement? {
    debug("visitSimpleUserType")
    return super.visitSimpleUserType(ctx)
  }

  override fun visitFunctionTypeParameters(ctx: KotlinParser.FunctionTypeParametersContext?): LanguageElement? {
    debug("visitFunctionTypeParameters")
    return super.visitFunctionTypeParameters(ctx)
  }

  override fun visitTypeConstraints(ctx: KotlinParser.TypeConstraintsContext?): LanguageElement? {
    debug("visitTypeConstraints")
    return super.visitTypeConstraints(ctx)
  }

  override fun visitTypeConstraint(ctx: KotlinParser.TypeConstraintContext?): LanguageElement? {
    debug("visitTypeConstraint")
    return super.visitTypeConstraint(ctx)
  }

  override fun visitBlock(ctx: KotlinParser.BlockContext?): LanguageElement? {
    debug("visitBlock")
    return super.visitBlock(ctx)
  }

  override fun visitStatements(ctx: KotlinParser.StatementsContext?): LanguageElement? {
    debug("visitStatements")
    return super.visitStatements(ctx)
  }

  override fun visitStatement(ctx: KotlinParser.StatementContext?): LanguageElement? {
    debug("visitStatement")
    return super.visitStatement(ctx)
  }

  override fun visitDeclaration(ctx: KotlinParser.DeclarationContext?): LanguageElement? {
    debug("visitDeclaration")
    return super.visitDeclaration(ctx)
  }

  override fun visitAssignment(ctx: KotlinParser.AssignmentContext?): LanguageElement? {
    debug("visitAssignment")
    return super.visitAssignment(ctx)
  }

  override fun visitExpression(ctx: KotlinParser.ExpressionContext?): LanguageElement? {
    debug("visitExpression")
    return super.visitExpression(ctx)
  }

  override fun visitDisjunction(ctx: KotlinParser.DisjunctionContext?): LanguageElement? {
    debug("visitDisjunction")
    return super.visitDisjunction(ctx)
  }

  override fun visitConjunction(ctx: KotlinParser.ConjunctionContext?): LanguageElement? {
    debug("visitConjunction")
    return super.visitConjunction(ctx)
  }

  override fun visitEquality(ctx: KotlinParser.EqualityContext?): LanguageElement? {
    debug("visitEquality")
    return super.visitEquality(ctx)
  }

  override fun visitComparison(ctx: KotlinParser.ComparisonContext?): LanguageElement? {
    debug("visitComparison")
    return super.visitComparison(ctx)
  }

  override fun visitInfixOperation(ctx: KotlinParser.InfixOperationContext?): LanguageElement? {
    debug("visitInfixOperation")
    return super.visitInfixOperation(ctx)
  }

  override fun visitElvisExpression(ctx: KotlinParser.ElvisExpressionContext?): LanguageElement? {
    debug("visitElvisExpression")
    return super.visitElvisExpression(ctx)
  }

  override fun visitInfixFunctionCall(ctx: KotlinParser.InfixFunctionCallContext?): LanguageElement? {
    debug("visitInfixFunctionCall")
    return super.visitInfixFunctionCall(ctx)
  }

  override fun visitRangeExpression(ctx: KotlinParser.RangeExpressionContext?): LanguageElement? {
    debug("visitRangeExpression")
    return super.visitRangeExpression(ctx)
  }

  override fun visitAdditiveExpression(ctx: KotlinParser.AdditiveExpressionContext?): LanguageElement? {
    debug("visitAdditiveExpression")
    return super.visitAdditiveExpression(ctx)
  }

  override fun visitMultiplicativeExpression(ctx: KotlinParser.MultiplicativeExpressionContext?): LanguageElement? {
    debug("visitMultiplicativeExpression")
    return super.visitMultiplicativeExpression(ctx)
  }

  override fun visitAsExpression(ctx: KotlinParser.AsExpressionContext?): LanguageElement? {
    debug("visitAsExpression")
    return super.visitAsExpression(ctx)
  }

  override fun visitPrefixUnaryExpression(ctx: KotlinParser.PrefixUnaryExpressionContext?): LanguageElement? {
    debug("visitPrefixUnaryExpression")
    return super.visitPrefixUnaryExpression(ctx)
  }

  override fun visitUnaryPrefix(ctx: KotlinParser.UnaryPrefixContext?): LanguageElement? {
    debug("visitUnaryPrefix")
    return super.visitUnaryPrefix(ctx)
  }

  override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext?): LanguageElement? {
    debug("visitPostfixUnaryExpression")
    return super.visitPostfixUnaryExpression(ctx)
  }

  override fun visitPostfixUnarySuffix(ctx: KotlinParser.PostfixUnarySuffixContext?): LanguageElement? {
    debug("visitPostfixUnarySuffix")
    return super.visitPostfixUnarySuffix(ctx)
  }

  override fun visitDirectlyAssignableExpression(
    ctx: KotlinParser.DirectlyAssignableExpressionContext
  ): LanguageElement? {
    return super.visitDirectlyAssignableExpression(ctx)
  }

  override fun visitAssignableExpression(ctx: KotlinParser.AssignableExpressionContext?): LanguageElement? {
    debug("visitAssignableExpression")
    return super.visitAssignableExpression(ctx)
  }

  override fun visitAssignableSuffix(ctx: KotlinParser.AssignableSuffixContext?): LanguageElement? {
    debug("visitAssignableSuffix")
    return super.visitAssignableSuffix(ctx)
  }

  override fun visitIndexingSuffix(ctx: KotlinParser.IndexingSuffixContext?): LanguageElement? {
    debug("visitIndexingSuffix")
    return super.visitIndexingSuffix(ctx)
  }

  override fun visitNavigationSuffix(ctx: KotlinParser.NavigationSuffixContext?): LanguageElement? {
    debug("visitNavigationSuffix")
    return super.visitNavigationSuffix(ctx)
  }

  override fun visitCallSuffix(ctx: KotlinParser.CallSuffixContext?): LanguageElement? {
    debug("visitCallSuffix")
    return super.visitCallSuffix(ctx)
  }

  override fun visitAnnotatedLambda(ctx: KotlinParser.AnnotatedLambdaContext?): LanguageElement? {
    debug("visitAnnotatedLambda")
    return super.visitAnnotatedLambda(ctx)
  }

  override fun visitValueArguments(ctx: KotlinParser.ValueArgumentsContext?): LanguageElement? {
    debug("visitValueArguments")
    return super.visitValueArguments(ctx)
  }

  override fun visitTypeArguments(ctx: KotlinParser.TypeArgumentsContext?): LanguageElement? {
    debug("visitTypeArguments")
    return super.visitTypeArguments(ctx)
  }

  override fun visitTypeProjection(ctx: KotlinParser.TypeProjectionContext?): LanguageElement? {
    debug("visitTypeProjection")
    return super.visitTypeProjection(ctx)
  }

  override fun visitTypeProjectionModifiers(ctx: KotlinParser.TypeProjectionModifiersContext?): LanguageElement? {
    debug("visitTypeProjectionModifiers")
    return super.visitTypeProjectionModifiers(ctx)
  }

  override fun visitTypeProjectionModifier(ctx: KotlinParser.TypeProjectionModifierContext?): LanguageElement? {
    debug("visitTypeProjectionModifier")
    return super.visitTypeProjectionModifier(ctx)
  }

  override fun visitValueArgument(ctx: KotlinParser.ValueArgumentContext?): LanguageElement? {
    debug("visitValueArgument")
    return super.visitValueArgument(ctx)
  }

  override fun visitPrimaryExpression(ctx: KotlinParser.PrimaryExpressionContext?): LanguageElement? {
    debug("visitPrimaryExpression")
    return super.visitPrimaryExpression(ctx)
  }

  override fun visitParenthesizedExpression(ctx: KotlinParser.ParenthesizedExpressionContext?): LanguageElement? {
    debug("visitParenthesizedExpression")
    return super.visitParenthesizedExpression(ctx)
  }

  override fun visitCollectionLiteral(ctx: KotlinParser.CollectionLiteralContext?): LanguageElement? {
    debug("visitCollectionLiteral")
    return super.visitCollectionLiteral(ctx)
  }

  override fun visitLiteralConstant(ctx: KotlinParser.LiteralConstantContext?): LanguageElement? {
    debug("visitLiteralConstant")
    return super.visitLiteralConstant(ctx)
  }

  override fun visitStringLiteral(ctx: KotlinParser.StringLiteralContext?): LanguageElement? {
    debug("visitStringLiteral")
    return super.visitStringLiteral(ctx)
  }

  override fun visitLineStringLiteral(ctx: KotlinParser.LineStringLiteralContext?): LanguageElement? {
    debug("visitLineStringLiteral")
    return super.visitLineStringLiteral(ctx)
  }

  override fun visitMultiLineStringLiteral(ctx: KotlinParser.MultiLineStringLiteralContext?): LanguageElement? {
    debug("visitMultiLineStringLiteral")
    return super.visitMultiLineStringLiteral(ctx)
  }

  override fun visitLineStringContent(ctx: KotlinParser.LineStringContentContext?): LanguageElement? {
    debug("visitLineStringContent")
    return super.visitLineStringContent(ctx)
  }

  override fun visitLineStringExpression(ctx: KotlinParser.LineStringExpressionContext?): LanguageElement? {
    debug("visitLineStringExpression")
    return super.visitLineStringExpression(ctx)
  }

  override fun visitMultiLineStringContent(ctx: KotlinParser.MultiLineStringContentContext?): LanguageElement? {
    debug("visitMultiLineStringContent")
    return super.visitMultiLineStringContent(ctx)
  }

  override fun visitMultiLineStringExpression(ctx: KotlinParser.MultiLineStringExpressionContext?): LanguageElement? {
    debug("visitMultiLineStringExpression")
    return super.visitMultiLineStringExpression(ctx)
  }

  override fun visitLambdaLiteral(ctx: KotlinParser.LambdaLiteralContext?): LanguageElement? {
    debug("visitLambdaLiteral")
    return super.visitLambdaLiteral(ctx)
  }

  override fun visitLambdaParameters(ctx: KotlinParser.LambdaParametersContext?): LanguageElement? {
    debug("visitLambdaParameters")
    return super.visitLambdaParameters(ctx)
  }

  override fun visitLambdaParameter(ctx: KotlinParser.LambdaParameterContext?): LanguageElement? {
    debug("visitLambdaParameter")
    return super.visitLambdaParameter(ctx)
  }

  override fun visitAnonymousFunction(ctx: KotlinParser.AnonymousFunctionContext?): LanguageElement? {
    debug("visitAnonymousFunction")
    return super.visitAnonymousFunction(ctx)
  }

  override fun visitFunctionLiteral(ctx: KotlinParser.FunctionLiteralContext?): LanguageElement? {
    debug("visitFunctionLiteral")
    return super.visitFunctionLiteral(ctx)
  }

  override fun visitObjectLiteral(ctx: KotlinParser.ObjectLiteralContext?): LanguageElement? {
    debug("visitObjectLiteral")
    return super.visitObjectLiteral(ctx)
  }

  override fun visitThisExpression(ctx: KotlinParser.ThisExpressionContext?): LanguageElement? {
    debug("visitThisExpression")
    return super.visitThisExpression(ctx)
  }

  override fun visitSuperExpression(ctx: KotlinParser.SuperExpressionContext?): LanguageElement? {
    debug("visitSuperExpression")
    return super.visitSuperExpression(ctx)
  }

  override fun visitControlStructureBody(ctx: KotlinParser.ControlStructureBodyContext?): LanguageElement? {
    debug("visitControlStructureBody")
    return super.visitControlStructureBody(ctx)
  }

  override fun visitIfExpression(ctx: KotlinParser.IfExpressionContext?): LanguageElement? {
    debug("visitIfExpression")
    return super.visitIfExpression(ctx)
  }

  override fun visitWhenExpression(ctx: KotlinParser.WhenExpressionContext?): LanguageElement? {
    debug("visitWhenExpression")
    return super.visitWhenExpression(ctx)
  }

  override fun visitWhenEntry(ctx: KotlinParser.WhenEntryContext?): LanguageElement? {
    debug("visitWhenEntry")
    return super.visitWhenEntry(ctx)
  }

  override fun visitWhenCondition(ctx: KotlinParser.WhenConditionContext?): LanguageElement? {
    debug("visitWhenCondition")
    return super.visitWhenCondition(ctx)
  }

  override fun visitRangeTest(ctx: KotlinParser.RangeTestContext?): LanguageElement? {
    debug("visitRangeTest")
    return super.visitRangeTest(ctx)
  }

  override fun visitTypeTest(ctx: KotlinParser.TypeTestContext?): LanguageElement? {
    debug("visitTypeTest")
    return super.visitTypeTest(ctx)
  }

  override fun visitTryExpression(ctx: KotlinParser.TryExpressionContext?): LanguageElement? {
    debug("visitTryExpression")
    return super.visitTryExpression(ctx)
  }

  override fun visitCatchBlock(ctx: KotlinParser.CatchBlockContext?): LanguageElement? {
    debug("visitCatchBlock")
    return super.visitCatchBlock(ctx)
  }

  override fun visitFinallyBlock(ctx: KotlinParser.FinallyBlockContext?): LanguageElement? {
    debug("visitFinallyBlock")
    return super.visitFinallyBlock(ctx)
  }

  override fun visitLoopStatement(ctx: KotlinParser.LoopStatementContext?): LanguageElement? {
    debug("visitLoopStatement")
    return super.visitLoopStatement(ctx)
  }

  override fun visitForStatement(ctx: KotlinParser.ForStatementContext?): LanguageElement? {
    debug("visitForStatement")
    return super.visitForStatement(ctx)
  }

  override fun visitWhileStatement(ctx: KotlinParser.WhileStatementContext?): LanguageElement? {
    debug("visitWhileStatement")
    return super.visitWhileStatement(ctx)
  }

  override fun visitDoWhileStatement(ctx: KotlinParser.DoWhileStatementContext?): LanguageElement? {
    debug("visitDoWhileStatement")
    return super.visitDoWhileStatement(ctx)
  }

  override fun visitJumpExpression(ctx: KotlinParser.JumpExpressionContext?): LanguageElement? {
    debug("visitJumpExpression")
    return super.visitJumpExpression(ctx)
  }

  override fun visitCallableReference(ctx: KotlinParser.CallableReferenceContext?): LanguageElement? {
    debug("visitCallableReference")
    return super.visitCallableReference(ctx)
  }

  override fun visitAssignmentAndOperator(ctx: KotlinParser.AssignmentAndOperatorContext?): LanguageElement? {
    debug("visitAssignmentAndOperator")
    return super.visitAssignmentAndOperator(ctx)
  }

  override fun visitEqualityOperator(ctx: KotlinParser.EqualityOperatorContext?): LanguageElement? {
    debug("visitEqualityOperator")
    return super.visitEqualityOperator(ctx)
  }

  override fun visitComparisonOperator(ctx: KotlinParser.ComparisonOperatorContext?): LanguageElement? {
    debug("visitComparisonOperator")
    return super.visitComparisonOperator(ctx)
  }

  override fun visitInOperator(ctx: KotlinParser.InOperatorContext?): LanguageElement? {
    debug("visitInOperator")
    return super.visitInOperator(ctx)
  }

  override fun visitIsOperator(ctx: KotlinParser.IsOperatorContext?): LanguageElement? {
    debug("visitIsOperator")
    return super.visitIsOperator(ctx)
  }

  override fun visitAdditiveOperator(ctx: KotlinParser.AdditiveOperatorContext?): LanguageElement? {
    debug("visitAdditiveOperator")
    return super.visitAdditiveOperator(ctx)
  }

  override fun visitMultiplicativeOperator(ctx: KotlinParser.MultiplicativeOperatorContext?): LanguageElement? {
    debug("visitMultiplicativeOperator")
    return super.visitMultiplicativeOperator(ctx)
  }

  override fun visitAsOperator(ctx: KotlinParser.AsOperatorContext?): LanguageElement? {
    debug("visitAsOperator")
    return super.visitAsOperator(ctx)
  }

  override fun visitPrefixUnaryOperator(ctx: KotlinParser.PrefixUnaryOperatorContext?): LanguageElement? {
    debug("visitPrefixUnaryOperator")
    return super.visitPrefixUnaryOperator(ctx)
  }

  override fun visitPostfixUnaryOperator(ctx: KotlinParser.PostfixUnaryOperatorContext?): LanguageElement? {
    debug("visitPostfixUnaryOperator")
    return super.visitPostfixUnaryOperator(ctx)
  }

  override fun visitMemberAccessOperator(ctx: KotlinParser.MemberAccessOperatorContext?): LanguageElement? {
    debug("visitMemberAccessOperator")
    return super.visitMemberAccessOperator(ctx)
  }

  override fun visitModifiers(ctx: KotlinParser.ModifiersContext?): LanguageElement? {
    debug("visitModifiers")
    return super.visitModifiers(ctx)
  }

  override fun visitModifier(ctx: KotlinParser.ModifierContext?): LanguageElement? {
    debug("visitModifier")
    return super.visitModifier(ctx)
  }

  override fun visitClassModifier(ctx: KotlinParser.ClassModifierContext?): LanguageElement? {
    debug("visitClassModifier")
    return super.visitClassModifier(ctx)
  }

  override fun visitMemberModifier(ctx: KotlinParser.MemberModifierContext?): LanguageElement? {
    debug("visitMemberModifier")
    return super.visitMemberModifier(ctx)
  }

  override fun visitVisibilityModifier(ctx: KotlinParser.VisibilityModifierContext?): LanguageElement? {
    debug("visitVisibilityModifier")
    return super.visitVisibilityModifier(ctx)
  }

  override fun visitVarianceModifier(ctx: KotlinParser.VarianceModifierContext?): LanguageElement? {
    debug("visitVarianceModifier")
    return super.visitVarianceModifier(ctx)
  }

  override fun visitFunctionModifier(ctx: KotlinParser.FunctionModifierContext?): LanguageElement? {
    debug("visitFunctionModifier")
    return super.visitFunctionModifier(ctx)
  }

  override fun visitPropertyModifier(ctx: KotlinParser.PropertyModifierContext?): LanguageElement? {
    debug("visitPropertyModifier")
    return super.visitPropertyModifier(ctx)
  }

  override fun visitInheritanceModifier(ctx: KotlinParser.InheritanceModifierContext?): LanguageElement? {
    debug("visitInheritanceModifier")
    return super.visitInheritanceModifier(ctx)
  }

  override fun visitParameterModifier(ctx: KotlinParser.ParameterModifierContext?): LanguageElement? {
    debug("visitParameterModifier")
    return super.visitParameterModifier(ctx)
  }

  override fun visitReificationModifier(ctx: KotlinParser.ReificationModifierContext?): LanguageElement? {
    debug("visitReificationModifier")
    return super.visitReificationModifier(ctx)
  }

  override fun visitPlatformModifier(ctx: KotlinParser.PlatformModifierContext?): LanguageElement? {
    debug("visitPlatformModifier")
    return super.visitPlatformModifier(ctx)
  }

  override fun visitLabel(ctx: KotlinParser.LabelContext?): LanguageElement? {
    debug("visitLabel")
    return super.visitLabel(ctx)
  }

  override fun visitAnnotation(ctx: KotlinParser.AnnotationContext?): LanguageElement? {
    debug("visitAnnotation")
    return super.visitAnnotation(ctx)
  }

  override fun visitSingleAnnotation(ctx: KotlinParser.SingleAnnotationContext?): LanguageElement? {
    debug("visitSingleAnnotation")
    return super.visitSingleAnnotation(ctx)
  }

  override fun visitMultiAnnotation(ctx: KotlinParser.MultiAnnotationContext?): LanguageElement? {
    debug("visitMultiAnnotation")
    return super.visitMultiAnnotation(ctx)
  }

  override fun visitAnnotationUseSiteTarget(ctx: KotlinParser.AnnotationUseSiteTargetContext?): LanguageElement? {
    debug("visitAnnotationUseSiteTarget")
    return super.visitAnnotationUseSiteTarget(ctx)
  }

  override fun visitUnescapedAnnotation(ctx: KotlinParser.UnescapedAnnotationContext?): LanguageElement? {
    debug("visitUnescapedAnnotation")
    return super.visitUnescapedAnnotation(ctx)
  }

  override fun visitSimpleIdentifier(ctx: KotlinParser.SimpleIdentifierContext?): LanguageElement? {
    debug("visitSimpleIdentifier")
    return super.visitSimpleIdentifier(ctx)
  }

  override fun visitIdentifier(ctx: KotlinParser.IdentifierContext?): LanguageElement? {
    debug("visitIdentifier")
    return super.visitIdentifier(ctx)
  }

  override fun visitShebangLine(ctx: KotlinParser.ShebangLineContext?): LanguageElement? {
    debug("visitShebangLine")
    return super.visitShebangLine(ctx)
  }

  override fun visitQuest(ctx: KotlinParser.QuestContext?): LanguageElement? {
    debug("visitQuest")
    return super.visitQuest(ctx)
  }

  override fun visitElvis(ctx: KotlinParser.ElvisContext?): LanguageElement? {
    debug("visitElvis")
    return super.visitElvis(ctx)
  }

  override fun visitSafeNav(ctx: KotlinParser.SafeNavContext?): LanguageElement? {
    debug("visitSafeNav")
    return super.visitSafeNav(ctx)
  }

  override fun visitExcl(ctx: KotlinParser.ExclContext?): LanguageElement? {
    debug("visitExcl")
    return super.visitExcl(ctx)
  }

  override fun visitSemi(ctx: KotlinParser.SemiContext?): LanguageElement? {
    debug("visitSemi")
    return super.visitSemi(ctx)
  }

  override fun visitSemis(ctx: KotlinParser.SemisContext?): LanguageElement? {
    debug("visitSemis")
    return super.visitSemis(ctx)
  }

  override fun visitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?): LanguageElement? {
    debug("visitFunctionDeclaration")
    val function = ctx?.let(::toFunction)
    if (function != null) {
      mutableFunctions.add(function)
    }
    return super.visitFunctionDeclaration(ctx)
  }

  private fun toFunction(ctx: KotlinParser.FunctionDeclarationContext): Function {
    val startLine = ctx.start.line
    val endLine = ctx.stop.line
    val functionSimpleIdentifier = ctx.simpleIdentifier()
    val functionName = functionSimpleIdentifier.text
    val lineNumber = functionSimpleIdentifier.start.line
    val functionNameStartIndex = functionSimpleIdentifier.start.charPositionInLine
    val functionIdentifier = Identifier(functionName, lineNumber, functionNameStartIndex)

    val parameters = ctx.functionValueParameters().functionValueParameter()
      .map { functionValueParameterContext -> identifierAndType(functionValueParameterContext) }
      .map { (identifier, type) -> Parameter(identifier, type) }

    return Function(startLine, endLine, Signature(functionIdentifier, parameters))
  }

  private fun identifierAndType(
    context: KotlinParser.FunctionValueParameterContext
  ): Pair<Identifier, Type> {
    val parameter = context.parameter()

    val parameterSimpleIdentifier = parameter.simpleIdentifier()
    val identifier = Identifier(
      parameterSimpleIdentifier.text,
      parameterSimpleIdentifier.start.line,
      parameterSimpleIdentifier.start.charPositionInLine
    )

    return identifier to Type(parameter.type_().text)
  }

  private fun debug(message: String) {
    if (DEBUG) println(message)
  }
}
