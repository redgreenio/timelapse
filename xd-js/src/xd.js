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

function setupListenersForIdentifiers(identifierSpanElements, _$ = $) { // eslint-disable-line no-unused-vars
  function handleIdentifierSelections(span) {
    const matchingIdentifierSpans = selectMatchingIdentifierSpans(span);

    const spanClasses = span.attr('class');
    if (spanClasses.includes('selected') || (spanClasses.includes('selected') && spanClasses.includes('highlight'))) {
      matchingIdentifierSpans.removeClass('selected');
      matchingIdentifierSpans.removeClass('highlight');
      return;
    }

    const previouslySelectedSpans = _$(selectSpansWithClass('selected'));
    previouslySelectedSpans.removeClass('selected');

    matchingIdentifierSpans.addClass('highlight selected');
  }

  identifierSpanElements
    .toArray()
    .map((spanElement) => _$(spanElement))
    .forEach((span) => {
      span.mouseenter(() => selectMatchingIdentifierSpans(span).addClass('highlight'));
      span.mouseleave(() => selectMatchingIdentifierSpans(span).removeClass('highlight'));
      span.click(() => handleIdentifierSelections(span));
    });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
  selectMatchingIdentifierSpans,
};
