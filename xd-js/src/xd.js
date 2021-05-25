const $ = require('jquery');

module.exports = {
  selectOccurrences: (window, identifier) => $(window).find(`[data-identifier='${identifier}']`),

  getIdentifier: (window, htmlElement) => $(window)(htmlElement).data('identifier'),
};
