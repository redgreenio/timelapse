require('mocha');

const approvals = require('approvals');

const approvalsDir = 'test/approved/interactions';

const $ = require('jquery');

const { JSDOM } = require('jsdom');

const fs = require('fs');

const xd = require('../src/xd');

const { mouseEntersIdentifier, mouseLeavesIdentifier, handleIdentifierClick } = xd;

function jsdomForHtmlAsset(fileName) {
  return new JSDOM(fs.readFileSync(`./test/assets/interactions/${fileName}`));
}

function verifyHtml(test, htmlElement) {
  const getHtmlString = (element) => element.closest('html').outerHTML.toString();
  approvals.verify(approvalsDir, test.title, getHtmlString(htmlElement));
}

describe('XD interactions', () => {
  describe('unselected-unfocused', () => {
    it('unselected-unfocused to highlight', function () {
      // given
      const unselectedHtmlJsdom = jsdomForHtmlAsset('unselected-unfocused.html');
      const htmlSpanElement = unselectedHtmlJsdom.window.document.querySelector('span');

      // when
      const highlightedSpans = mouseEntersIdentifier(htmlSpanElement, $(unselectedHtmlJsdom.window));
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
      const unselectedSpans = mouseLeavesIdentifier(htmlSpanElement, $(highlightedHtmlJsdom.window));
      const spanElement = unselectedSpans.toArray()[0];

      // then
      verifyHtml(this.test, spanElement);
    });

    it('highlight to selected-focused', function () {
      // given
      const jsdom = jsdomForHtmlAsset('highlighted.html');
      const htmlSpanElement = jsdom.window.document.querySelector('span');

      // when
      handleIdentifierClick(htmlSpanElement, $(jsdom.window));

      // then
      verifyHtml(this.test, jsdom.window.document.documentElement);
    });
  });

  describe('selected-focused', () => {
    it('selected-focused to unselected', function () {
      // given
      const jsdom = jsdomForHtmlAsset('selected-focused.html');
      const htmlSpanElement = jsdom.window.document.querySelector('span');

      // when
      handleIdentifierClick(htmlSpanElement, $(jsdom.window));

      // then
      verifyHtml(this.test, jsdom.window.document.documentElement);
    });

    it('selected-focused to selected-unfocused', function () {
      // given
      const jsdom = jsdomForHtmlAsset('selected-focused.html');
      const htmlSpanElement = jsdom.window.document.querySelector('span');

      // when
      const unfocusedSpans = mouseLeavesIdentifier(htmlSpanElement, $(jsdom.window));
      const spanElement = unfocusedSpans.toArray()[0];

      // then
      verifyHtml(this.test, spanElement);
    });
  });

  describe('selected-unfocused', () => {
    it('selected-unfocused to selected-focused', function () {
      // given
      const jsdom = jsdomForHtmlAsset('selected-unfocused.html');
      const htmlSpanElement = jsdom.window.document.querySelector('span');

      // when
      const highlightedSpans = mouseEntersIdentifier(htmlSpanElement, $(jsdom.window));
      const spanElement = highlightedSpans.toArray()[0];

      // then
      verifyHtml(this.test, spanElement);
    });
  });
});
