function selectOccurrences(identifier, jquery = $) { return jquery.find(`[data-identifier='${identifier}']`); }

function getIdentifier(htmlElement, jquery = $) { return jquery(htmlElement).data('identifier'); }

module.exports = {
  selectOccurrences,
  getIdentifier,
};
