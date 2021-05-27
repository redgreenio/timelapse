require('mocha');

const approvals = require('approvals');

const approvalsDir = 'test/approved/interactions';

const $ = require('jquery');

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const { highlight, removeHighlight } = xd;

function jsdomForHtmlAsset(fileName) {
  return new JSDOM(fs.readFileSync(`./test/assets/interactions/${fileName}`));
}

function verifyHtml(test, htmlElement) {
  const getHtmlString = (element) => element.closest('html').outerHTML.toString();
  approvals.verify(approvalsDir, test.title, getHtmlString(htmlElement));
}

describe('XD interactions', () => {
  describe('unselected', () => {
    it('unselected to highlight', function () {
      // given
      const unselectedHtmlJsdom = jsdomForHtmlAsset('unselected.html');
      const htmlSpanElement = unselectedHtmlJsdom.window.document.querySelector('span');

      // when
      const highlightedSpans = highlight(htmlSpanElement, $(unselectedHtmlJsdom.window));
      const spanElement = highlightedSpans.toArray()[0];

      // then
      verifyHtml(this.test, spanElement);
    });
  });

  describe('highlight', () => {
    it('highlight to unselected', function () {
      // given
      const highlightedHtmlJsdom = jsdomForHtmlAsset('highlighted.html');
      const htmlSpanElement = highlightedHtmlJsdom.window.document.querySelector('span');

      // when
      const unselectedSpans = removeHighlight(htmlSpanElement, $(highlightedHtmlJsdom.window));
      const spanElement = unselectedSpans.toArray()[0];

      // then
      verifyHtml(this.test, spanElement);
    });
  });
});
