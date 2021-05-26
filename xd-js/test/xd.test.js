const chai = require('chai');

const { should } = chai;
should();

const $ = require('jquery');

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const { selectOccurrences, getIdentifier } = xd;

describe('selectOccurrences', () => {
  const addFunctionHtml = fs.readFileSync('./test/assets/add-function.html');
  const { window } = new JSDOM(addFunctionHtml);
  const jquery = $(window);

  it("should select all elements with identifier 'a'", () => {
    // when
    const occurrences = selectOccurrences('a', jquery);

    // then
    occurrences.should.have.length(2);
  });

  it("should select all elements with identifier 'b'", () => {
    // when
    const occurrences = selectOccurrences('b', jquery);

    // then
    occurrences.should.have.length(2);
  });

  it('should return an empty list if the identifier does not exist', () => {
    // when
    const occurrences = selectOccurrences('z', jquery);

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
