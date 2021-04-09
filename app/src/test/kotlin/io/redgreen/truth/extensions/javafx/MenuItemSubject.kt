package io.redgreen.truth.extensions.javafx

import com.google.common.truth.Fact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import javafx.scene.control.MenuItem

class MenuItemSubject private constructor(
  failureMetadata: FailureMetadata,
  subject: MenuItem
) : Subject(failureMetadata, subject) {
  private val actual = subject

  companion object {
    private val MENU_ITEM_SUBJECT_FACTORY = Factory(::MenuItemSubject)

    fun assertThat(menuItem: MenuItem): MenuItemSubject =
      assertAbout(MENU_ITEM_SUBJECT_FACTORY).that(menuItem)

    fun menuItems(): Factory<MenuItemSubject, MenuItem> =
      MENU_ITEM_SUBJECT_FACTORY
  }

  fun hasText(text: String): MenuItemSubject {
    check("text").that(actual.text).isEqualTo(text)
    return this
  }

  fun isEnabled(): MenuItemSubject {
    if (actual.isDisable) {
      failWithActual(Fact.simpleFact("expected to be enabled"))
    }
    return this
  }

  fun isDisabled(): MenuItemSubject {
    if (!actual.isDisable) {
      failWithActual(Fact.simpleFact("expected to be disabled"))
    }
    return this
  }
}
