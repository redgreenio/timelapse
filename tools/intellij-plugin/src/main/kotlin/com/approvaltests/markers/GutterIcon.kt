package com.approvaltests.markers

enum class GutterIcon {
  MISSING_MISSING {
    override val iconResourceName: String = "missing-missing"
  },

  MISSING_PRESENT {
    override val iconResourceName: String = "missing-present"
  },

  PRESENT_MISSING {
    override val iconResourceName: String = "present-missing"
  },

  PRESENT_EMPTY {
    override val iconResourceName: String = "present-empty"
  },

  PRESENT_PRESENT_SAME {
    override val iconResourceName: String = "present-present-same"
  },

  PRESENT_PRESENT_DIFFERENT {
    override val iconResourceName: String = "present-present-different"
  };

  abstract val iconResourceName: String
}
