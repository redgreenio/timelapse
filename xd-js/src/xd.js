const $ = require('jquery');

module.exports = {
  jQueryProxy: () => $,

  selectOccurrences: (identifier) => module.exports.jQueryProxy()().find(`[data-identifier='${identifier}']`),

  getIdentifier: (htmlElement) => module.exports.jQueryProxy()()(htmlElement).data('identifier'),
};
