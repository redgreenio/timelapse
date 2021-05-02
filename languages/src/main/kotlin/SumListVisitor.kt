class SumListVisitor : listBaseVisitor<Int>() {
  override fun visitList(context: listParser.ListContext): Int {
    context.elems() ?: return 0
    return visitElems(context.elems())
  }

  override fun visitElems(context: listParser.ElemsContext): Int {
    var sum = 0
    for (elemContext in context.elem()) {
      sum += this.visitElem(elemContext)
    }
    return sum
  }

  override fun visitElem(context: listParser.ElemContext): Int {
    return Integer.parseInt(context.text)
  }
}
