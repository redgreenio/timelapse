function selectOccurrences(identifier, jquery = $) { return jquery.find(`[data-identifier='${identifier}']`); }

function getIdentifier(htmlElement, jquery = $) { return jquery(htmlElement).data('identifier'); }

function selectSpansWithClass(cssClassName, jquery = $) { return jquery(`span.${cssClassName}`); }

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
};
