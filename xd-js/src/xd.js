function selectOccurrences(identifier, _$ = $) { return _$.find(`[data-identifier='${identifier}']`); }

function getIdentifier(htmlElement, _$ = $) { return _$(htmlElement).data('identifier'); }

function selectSpansWithClass(cssClassName, _$ = $) { return _$(`span.${cssClassName}`).toArray(); }

function selectMatchingIdentifierSpans(spanElement, _$ = $) {
  const identifier = getIdentifier(spanElement, _$);
  const occurrences = selectOccurrences(identifier, _$);
  return _$(occurrences);
}

function setupListenersForIdentifiers(identifierSpanElements, _$ = $) { // eslint-disable-line no-unused-vars
  identifierSpanElements.forEach((span) => {
    _$(span).mouseenter(() => selectMatchingIdentifierSpans(span).addClass('highlight'));
    _$(span).mouseleave(() => selectMatchingIdentifierSpans(span).removeClass('highlight'));
  });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
};
