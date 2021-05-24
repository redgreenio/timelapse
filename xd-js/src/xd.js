const $ = require('jquery')

module.exports = {
  selectOccurrences: (window, identifier) => {
    return $(window).find(`[data-identifier='${identifier}']`);
  },
};
