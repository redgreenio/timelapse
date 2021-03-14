package toys

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import java.io.File

private val classLoader = X::class.java.classLoader

fun main() {
  val mobiusStoreCompilationUnit = JavaParser(ParserConfiguration())
    .parse(readJavaSource())
    .result
    .get()
  val mobiusStoreTypeDeclaration = mobiusStoreCompilationUnit.types.first.get()

  mobiusStoreTypeDeclaration
    .fields
    .onEach { println(it.variables) }

  mobiusStoreTypeDeclaration
    .constructors
    .take(1)
    .map { it.body }
    .flatMap { it.statements }
    .onEach { println(it) }

  mobiusStoreTypeDeclaration
    .methods
    .onEach { println(it.name) }
}

private fun readJavaSource() = File(
  classLoader.resources("toys/MobiusStore.java")
    .findFirst()
    .get()
    .toURI()
)
  .readText()

interface X
