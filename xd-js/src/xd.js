const jquery = require('jquery');

module.exports = {
  jQueryProxy: () => jquery,

  selectOccurrences: (identifier) => module.exports.jQueryProxy()().find(`[data-identifier='${identifier}']`),

  getIdentifier: (window, htmlElement) => module.exports.jQueryProxy()(window)(htmlElement).data('identifier'),
};
