function selectOccurrences(identifier, _$ = $) {
  return _$.find(`[data-identifier='${identifier}']`);
}

function getIdentifier(htmlElement, _$ = $) {
  return _$(htmlElement).data('identifier');
}

function selectSpansWithClass(cssClassName, _$ = $) {
  return _$(`span.${cssClassName}`);
}

function selectMatchingIdentifierSpans(spanElement, _$ = $) {
  const identifier = getIdentifier(spanElement, _$);
  const occurrences = selectOccurrences(identifier, _$);
  return _$(occurrences);
}

function setupListenersForIdentifiers(identifierSpanElements, _$ = $) { // eslint-disable-line no-unused-vars
  identifierSpanElements
    .toArray()
    .map((spanElement) => _$(spanElement))
    .forEach((span) => {
      span.mouseenter(() => selectMatchingIdentifierSpans(span).addClass('highlight'));
      span.mouseleave(() => selectMatchingIdentifierSpans(span).removeClass('highlight'));

      span.click(() => {
        const previouslySelectedSpans = _$(selectSpansWithClass('selected'));
        previouslySelectedSpans.removeClass('selected');

        const matchingIdentifierSpans = selectMatchingIdentifierSpans(span);
        matchingIdentifierSpans.removeClass('highlight');
        matchingIdentifierSpans.addClass('selected');
      });
    });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
  selectMatchingIdentifierSpans,
};
