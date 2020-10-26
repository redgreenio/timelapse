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
    tree.insert("root/app")

    assertThat(tree.root.children)
      .containsExactly(
        Node("app"),
      )
  }

  @Test
  fun `it should insert multiple child nodes`() {
    with(tree) {
      insert("root/app")
      insert("root/buildscripts")
    }

    assertThat(tree.root.children)
      .containsExactly(
        Node("app"),
        Node("buildscripts"),
      )
  }

  @Test
  fun `it should add a grandchild`() {
    tree.insert("root/app/main")

    assertThat(tree.root.children)
      .containsExactly(Node("app", mutableListOf(Node("main"))))
  }

  @Test
  fun `it should add multiple grandchildren`() {
    with(tree) {
      insert("root/app/main")
      insert("root/app/test")
    }

    val rootChildren = tree.root.children
    val app = rootChildren.first()
    assertThat(app.children)
      .containsExactly(
        Node("main"),
        Node("test"),
      )
  }

  @Test
  fun `it should add several levels of descendants`() {
    with(tree) {
      insert("root/app/main")
      insert("root/app/test")
      insert("root/buildscripts")
    }

    assertThat(tree.root.children)
      .containsExactly(
        Node("app", mutableListOf(Node("main"), Node("test"))),
        Node("buildscripts")
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
}
