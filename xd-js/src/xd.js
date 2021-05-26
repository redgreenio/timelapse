function selectOccurrences(identifier, jquery = $) { return jquery.find(`[data-identifier='${identifier}']`); }

function getIdentifier(htmlElement, jquery = $) { return jquery(htmlElement).data('identifier'); }

function selectSpansWithClass(cssClassName, jquery = $) { return jquery(`span.${cssClassName}`).toArray(); }

function setupListenersForIdentifiers(identifierSpanElements, jquery = $) { // eslint-disable-line no-unused-vars
  identifierSpanElements.forEach((span) => {
    jquery(span).mouseenter(() => {
      const identifier = getIdentifier(span, jquery);
      const occurrences = selectOccurrences(identifier, jquery);
      jquery(occurrences).addClass('highlight');
    });

    jquery(span).mouseleave(() => {
      const identifier = getIdentifier(span, jquery);
      const occurrences = selectOccurrences(identifier, jquery);
      jquery(occurrences).removeClass('highlight');
    });
  });
}

module.exports = {
  selectOccurrences,
  getIdentifier,
  selectSpansWithClass,
};
