const assert = require('assert');

const { JSDOM } = require('jsdom');

const { window } = new JSDOM('<!DOCTYPE html><p>Hello world</p>');
const $ = require('jquery')(window);

describe('a test environment', () => {
  it('should have mocha setup', () => {
    assert.strictEqual(true, true);
  });

  it('should have jsdom and jquery setup', () => {
    assert.strictEqual($('p').text(), 'Hello world');
  });
});
