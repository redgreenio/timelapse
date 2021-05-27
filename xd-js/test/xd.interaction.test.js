require('chai');

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

function htmlString(htmlElement) {
  return htmlElement.closest('html').outerHTML.toString();
}

describe('XD interactions', () => {
  describe('unselected', () => {
    it('unselected to highlight', function () {
      // given
      const unselectedHtmlJsdom = jsdomForHtmlAsset('unselected.html');
      const htmlSpanElement = unselectedHtmlJsdom.window.document.querySelector('span');

      // when
      const highlightedSpans = highlight(htmlSpanElement, $(unselectedHtmlJsdom.window));

      // then
      const spanElement = highlightedSpans.toArray()[0];
      approvals.verify(approvalsDir, this.test.title, htmlString(spanElement));
    });
  });

  describe('highlight', () => {
    it('highlight to unselected', function () {
      // given
      const highlightedHtmlJsdom = jsdomForHtmlAsset('highlighted.html');
      const htmlSpanElement = highlightedHtmlJsdom.window.document.querySelector('span');

      // when
      const unselectedSpans = removeHighlight(htmlSpanElement, $(highlightedHtmlJsdom.window));

      // then
      const spanElement = unselectedSpans.toArray()[0];
      approvals.verify(approvalsDir, this.test.title, htmlString(spanElement));
    });
  });
});
