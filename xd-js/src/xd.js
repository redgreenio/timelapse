const jquery = require('jquery');

module.exports = {
  jQueryProxy: () => jquery,

  selectOccurrences: (window, identifier) => module.exports.jQueryProxy()(window).find(`[data-identifier='${identifier}']`),

  getIdentifier: (window, htmlElement) => module.exports.jQueryProxy()(window)(htmlElement).data('identifier'),
};
