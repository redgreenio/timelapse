package io.redgreen.timelapse.git.usecases

import io.redgreen.timelapse.git.model.Ancestors
import io.redgreen.timelapse.git.model.Ancestors.Many
import io.redgreen.timelapse.git.model.Ancestors.None
import io.redgreen.timelapse.git.model.Ancestors.One
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.HOUR
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance
import java.util.Date
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit

class CommitInfoUseCase(
  private val repository: Repository
) {
  sealed class Property<T> {
    abstract fun get(revCommit: RevCommit): T

    object ShortMessage : Property<String>() {
      override fun get(revCommit: RevCommit): String =
        revCommit.shortMessage
    }

    object FullMessage : Property<String>() {
      override fun get(revCommit: RevCommit): String =
        revCommit.fullMessage
    }

    object Author : Property<Identity>() {
      override fun get(revCommit: RevCommit): Identity {
        val authorIdent = revCommit.authorIdent
        return Identity(authorIdent.name, authorIdent.emailAddress)
      }
    }

    object AuthoredLocalDateTime : Property<LocalDateTime>() {
      override fun get(revCommit: RevCommit): LocalDateTime =
        getLocalDateTime(revCommit.authorIdent.`when`)
    }

    object Committer : Property<Identity>() {
      override fun get(revCommit: RevCommit): Identity {
        val committerIdent = revCommit.committerIdent
        return Identity(committerIdent.name, committerIdent.emailAddress)
      }
    }

    object CommittedLocalDateTime : Property<LocalDateTime>() {
      override fun get(revCommit: RevCommit): LocalDateTime =
        getLocalDateTime(revCommit.committerIdent.`when`)
    }

    object Parent : Property<Ancestors>() {
      override fun get(revCommit: RevCommit): Ancestors = when (revCommit.parentCount) {
        0 -> None
        1 -> One(revCommit.parents.first().name)
        else -> Many(revCommit.parents.map(RevCommit::name).toList())
      }
    }

    object ParentCount : Property<Int>() {
      override fun get(revCommit: RevCommit): Int =
        revCommit.parentCount
    }

    object Encoding : Property<Charset>() {
      override fun get(revCommit: RevCommit): Charset =
        revCommit.encoding
    }

    internal fun getLocalDateTime(date: Date): LocalDateTime {
      val calendar = getInstance().apply {
        time = date
      }

      with(calendar) {
        // The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0
        val month = get(MONTH) + 1
        return LocalDateTime.of(get(YEAR), month, get(DAY_OF_MONTH), get(HOUR), get(MINUTE))
      }
    }
  }

  fun <T> invoke(
    commitHash: CommitHash,
    property: Property<T>,
  ): T =
    property.get(revCommitFor(commitHash))

  fun <A, B, T> invoke(
    commitHash: CommitHash,
    a: Property<A>,
    b: Property<B>,
    zip: (A, B) -> T
  ): T {
    val revCommit = revCommitFor(commitHash)
    return zip(a.get(revCommit), b.get(revCommit))
  }

  fun <A, B, C, T> invoke(
    commitHash: CommitHash,
    a: Property<A>,
    b: Property<B>,
    c: Property<C>,
    zip: (A, B, C) -> T
  ): T {
    val revCommit = revCommitFor(commitHash)
    return zip(a.get(revCommit), b.get(revCommit), c.get(revCommit))
  }

  fun <A, B, C, D, T> invoke(
    commitHash: CommitHash,
    a: Property<A>,
    b: Property<B>,
    c: Property<C>,
    d: Property<D>,
    zip: (A, B, C, D) -> T
  ): T {
    val revCommit = revCommitFor(commitHash)
    return zip(a.get(revCommit), b.get(revCommit), c.get(revCommit), d.get(revCommit))
  }

  fun <A, B, C, D, E, T> invoke(
    commitHash: CommitHash,
    a: Property<A>,
    b: Property<B>,
    c: Property<C>,
    d: Property<D>,
    e: Property<E>,
    zip: (A, B, C, D, E) -> T
  ): T {
    val revCommit = revCommitFor(commitHash)
    return zip(a.get(revCommit), b.get(revCommit), c.get(revCommit), d.get(revCommit), e.get(revCommit))
  }

  fun <A, B, C, D, E, F, T> invoke(
    commitHash: CommitHash,
    a: Property<A>,
    b: Property<B>,
    c: Property<C>,
    d: Property<D>,
    e: Property<E>,
    f: Property<F>,
    zip: (A, B, C, D, E, F) -> T
  ): T {
    val revCommit = revCommitFor(commitHash)
    return zip(
      a.get(revCommit),
      b.get(revCommit),
      c.get(revCommit),
      d.get(revCommit),
      e.get(revCommit),
      f.get(revCommit)
    )
  }

  private fun revCommitFor(commitHash: CommitHash): RevCommit =
    repository.parseCommit(repository.resolve(commitHash.value))
}
