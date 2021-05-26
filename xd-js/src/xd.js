function selectOccurrences(identifier, jquery = $) { return jquery.find(`[data-identifier='${identifier}']`); }

function getIdentifier(htmlElement, jquery = $) { return jquery(htmlElement).data('identifier'); }

function selectSpansWithClass(cssClassName, jquery = $) { return jquery(`span.${cssClassName}`).toArray(); }

function selectMatchingIdentifierSpans(spanElement, jquery = $) {
  const identifier = getIdentifier(spanElement, jquery);
  const occurrences = selectOccurrences(identifier, jquery);
  return jquery(occurrences);
}

function setupListenersForIdentifiers(identifierSpanElements, jquery = $) { // eslint-disable-line no-unused-vars
  identifierSpanElements.forEach((span) => {
    jquery(span).mouseenter(() => selectMatchingIdentifierSpans(span).addClass('highlight'));
    jquery(span).mouseleave(() => selectMatchingIdentifierSpans(span).removeClass('highlight'));
  });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
};
