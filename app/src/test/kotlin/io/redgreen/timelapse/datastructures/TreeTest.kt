package io.redgreen.timelapse.datastructures

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.datastructures.Tree.Node
import org.junit.jupiter.api.Test

class TreeTest {
  private val tree = Tree.create("root") { it.split('/') }

  @Test
  fun `it should have a root node`() {
    assertThat(tree.root)
      .isEqualTo(Node("root"))
  }

  @Test
  fun `it should insert a child node`() {
    tree.insert("root/.gitignore")

    assertThat(tree.root.children)
      .containsExactly(
        Node(".gitignore"),
      )
  }

  @Test
  fun `it should insert multiple child nodes`() {
    with(tree) {
      insert("root/.gitignore")
      insert("root/config.xml")
    }

    assertThat(tree.root.children)
      .containsExactly(
        Node(".gitignore"),
        Node("config.xml"),
      )
  }

  @Test
  fun `it should add a grandchild`() {
    tree.insert("root/app/package.json")

    println("Tree: ${tree.root}")
    assertThat(tree.root.children)
      .containsExactly(Node("app", mutableListOf(Node("package.json"))))
  }

  @Test
  fun `it should add multiple grandchildren`() {
    with(tree) {
      insert("root/app/package.json")
      insert("root/app/yarn.yml")
    }

    val rootChildren = tree.root.children
    val app = rootChildren.first()
    assertThat(app.children)
      .containsExactly(
        Node("package.json"),
        Node("yarn.yml"),
      )
  }

  @Test
  fun `it should add several levels of descendants`() {
    with(tree) {
      insert("root/app/package.json")
      insert("root/app/yarn.json")
      insert("root/config.xml")
    }

    assertThat(tree.root.children)
      .containsExactly(
        Node("app", mutableListOf(Node("package.json"), Node("yarn.json"))),
        Node("config.xml")
      )
  }

  @Test
  fun `it should insert descendants with colliding names`() {
    tree.insert("root/root")

    assertThat(tree.root.children)
      .containsExactly(
        Node("root")
      )
  }

  @Test
  fun `it should transform a tree with just the root`() {
    val transformedRootNode = tree.transform(AnotherNode.transformer)

    assertThat(transformedRootNode)
      .isEqualTo(AnotherNode(1))
  }

  @Test
  fun `it should map a tree into a different tree with the same structure`() {
    // given
    with(tree) {
      insert("root/app/.gitignore")
      insert("root/app/config.yml")
    }

    // when
    val transformedRootNode = tree.transform(AnotherNode.transformer)

    // then
    assertThat(transformedRootNode.value)
      .isEqualTo(1)
    assertThat(transformedRootNode.children)
      .containsExactly(
        AnotherNode(2, mutableListOf(AnotherNode(3), AnotherNode(4)))
      )
  }

  @Test
  fun `it should place branch nodes before leaf nodes on insertion`() {
    // when
    with(tree) {
      insert("root/app.xml")
      insert("root/buildscripts/build.gradle")
    }

    // then
    assertThat(tree.root.children)
      .containsExactly(
        Node("buildscripts", mutableListOf(Node("build.gradle"))),
        Node("app.xml")
      )
      .inOrder()
  }
}
