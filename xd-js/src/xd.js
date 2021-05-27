function selectOccurrences(identifier, enclosedElement = null, _$ = $) {
  if (enclosedElement == null) {
    return _$.find(`[data-identifier='${identifier}']`);
  }
  return _$(enclosedElement).closest('tbody').find(`[data-identifier='${identifier}']`);
}

function getIdentifier(htmlElement, _$ = $) {
  return _$(htmlElement).data('identifier');
}

function selectSpansWithClass(cssClassName, _$ = $) {
  return _$(`span.${cssClassName}`);
}

function selectMatchingIdentifierSpans(spanElement, _$ = $) {
  const identifier = getIdentifier(spanElement, _$);
  const occurrences = selectOccurrences(identifier, spanElement, _$);
  return _$(occurrences);
}

function highlight(span, _$ = $) {
  return selectMatchingIdentifierSpans(span, _$).addClass('highlight');
}

function removeHighlight(span, _$ = $) {
  return selectMatchingIdentifierSpans(span, _$).removeClass('highlight');
}

function handleIdentifierClick(span, _$ = $) {
  const matchingIdentifierSpans = selectMatchingIdentifierSpans(span, _$);

  const spanClasses = _$(span).attr('class');
  if (spanClasses.includes('selected') || (spanClasses.includes('selected') && spanClasses.includes('highlight'))) {
    matchingIdentifierSpans.removeClass('selected');
    matchingIdentifierSpans.removeClass('highlight');
    return;
  }

  const previouslySelectedSpans = _$(selectSpansWithClass('selected', _$));
  previouslySelectedSpans.removeClass('selected');

  matchingIdentifierSpans.addClass('highlight selected');
}

function setupListenersForIdentifiers(identifierSpanElements, _$ = $) { // eslint-disable-line no-unused-vars
  identifierSpanElements
    .toArray()
    .map((spanElement) => _$(spanElement))
    .forEach((span) => {
      span.mouseenter(() => highlight(span));
      span.mouseleave(() => removeHighlight(span));
      span.click(() => handleIdentifierClick(span));
    });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
  selectMatchingIdentifierSpans,
  mouseEntersIdentifier: highlight,
  mouseLeavesIdentifier: removeHighlight,
  handleIdentifierClick,
};
