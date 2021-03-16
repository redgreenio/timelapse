package affectedfiles.props.callback

import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext

interface AffectedFileContextChangeListener {
  fun onChange(context: AffectedFileContext)
}
