const chai = require('chai');

const { should } = chai;
should();

const $ = require('jquery');

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const {
  selectOccurrences, getIdentifier, selectSpansWithClass, selectMatchingIdentifierSpans,
} = xd;

describe('selectOccurrences', () => {
  const addFunctionHtml = fs.readFileSync('./test/assets/single-function.html');
  const { window } = new JSDOM(addFunctionHtml);
  const jquery = $(window);

  it("should select all elements with identifier 'a'", () => {
    // when
    const occurrences = selectOccurrences('a', null, jquery);

    // then
    occurrences.should.have.length(2);
  });

  it("should select all elements with identifier 'b'", () => {
    // when
    const occurrences = selectOccurrences('b', null, jquery);

    // then
    occurrences.should.have.length(2);
  });

  it('should return an empty list if the identifier does not exist', () => {
    // when
    const occurrences = selectOccurrences('z', null, jquery);

    // then
    occurrences.should.have.length(0);
  });
});

describe('getIdentifier', () => {
  it('should get the identifier from the data attribute', () => {
    // given
    const { window } = new JSDOM(('<span data-identifier=\'name\'>name</span>'));
    const spanHtmlElement = window.document.querySelector('span');
    const jquery = $(window);

    // when
    const identifier = getIdentifier(spanHtmlElement, jquery);

    // then
    identifier.should.equal('name');
  });
});

describe('selectSpansWithClass', () => {
  const addFunctionHtml = fs.readFileSync('./test/assets/single-function.html');
  const { window } = new JSDOM(addFunctionHtml);
  const jquery = $(window);

  it('should return empty if no spans with class is found', () => {
    // when
    const spansWithClass = selectSpansWithClass('css-class-404', jquery);

    // then
    spansWithClass.should.be.length(0);
  });

  it('should return spans with matching css classes', () => {
    // when
    const spansWithClass = selectSpansWithClass('identifier', jquery);

    // then
    spansWithClass.should.be.length(4);
  });
});

describe('selectMatchingIdentifierSpans', () => {
  it('should return spans with matching identifier', () => {
    // given
    const addFunctionHtml = fs.readFileSync('./test/assets/single-function.html');
    const jsdom = new JSDOM(addFunctionHtml);
    const { window } = jsdom;
    const jquery = $(window);

    const spanIdentifierElement = jsdom.window.document.querySelector('span');

    // when
    const identifierSpans = selectMatchingIdentifierSpans(spanIdentifierElement, jquery);

    // then
    identifierSpans.should.be.length(2);
  });
});

describe('multiple functions', () => {
  describe('selectMatchingIdentifierSpans', () => {
    it('should select spans within a given scope', () => {
      // given
      const multipleFunctionsHtml = fs.readFileSync('./test/assets/multiple-functions.html');
      const { window } = new JSDOM(multipleFunctionsHtml);
      const jquery = $(window);
      const firstSpan = window.document.querySelector('span.identifier');

      // when
      const identifierSpans = selectMatchingIdentifierSpans(firstSpan, jquery);

      // then
      identifierSpans.should.be.length(2);
    });
  });
});
