const { should } = require('chai');

should();

const approvals = require('approvals');

const approvalsDir = 'test/approved/canary';
const { JSDOM } = require('jsdom');

const { window } = new JSDOM('<!DOCTYPE html><p>Hello world</p>');

const $ = require('jquery')(window);

describe('a test environment', () => {
  it('should have mocha setup', () => {
    true.should.be.true; // eslint-disable-line
  });

  it('should have jsdom and jquery setup', () => {
    $('p').text().should.equal('Hello world');
  });

  it('should have approvals setup', function () {
    approvals.verify(approvalsDir, this.test.title, 'Hello, world!');
  });
});
