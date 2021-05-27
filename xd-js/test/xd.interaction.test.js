require('chai');

const approvals = require('approvals');

const approvalsDir = 'test/approved/interactions';

const $ = require('jquery');

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const { highlight } = xd;

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
      const aSpanElement = highlightedSpans.toArray()[0];
      approvals.verify(approvalsDir, this.test.title, htmlString(aSpanElement));
    });
  });
});
