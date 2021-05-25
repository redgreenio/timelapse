const jquery = require('jquery');

module.exports = {
  jQueryProxy: () => jquery,

  selectOccurrences: (identifier) => module.exports.jQueryProxy()().find(`[data-identifier='${identifier}']`),

  getIdentifier: (htmlElement) => module.exports.jQueryProxy()()(htmlElement).data('identifier'),
};
