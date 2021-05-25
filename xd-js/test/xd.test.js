const chai = require('chai');

const { should } = chai;
should();

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const { selectOccurrences, getIdentifier } = xd;

describe('selectOccurrences', () => {
  const addFunctionHtml = fs.readFileSync('./test/assets/add-function.html');
  const { window } = new JSDOM(addFunctionHtml);

  it("should select all elements with identifier 'a'", () => {
    // when
    const occurrences = selectOccurrences(window, 'a');

    // then
    occurrences.should.have.length(2);
  });

  it("should select all elements with identifier 'b'", () => {
    // when
    const occurrences = selectOccurrences(window, 'b');

    // then
    occurrences.should.have.length(2);
  });

  it('should return an empty list if the identifier does not exist', () => {
    // when
    const occurrences = selectOccurrences(window, 'z');

    // then
    occurrences.should.have.length(0);
  });
});

describe('getIdentifier', () => {
  it('should get the identifier from the data attribute', () => {
    // given
    const { window } = new JSDOM(('<span data-identifier=\'name\'>name</span>'));
    const spanHtmlElement = window.document.querySelector('span');

    // when
    const identifier = getIdentifier(window, spanHtmlElement);

    // then
    identifier.should.equal('name');
  });
});
