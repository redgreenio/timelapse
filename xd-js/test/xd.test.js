const assert = require("assert");

const {JSDOM} = require("jsdom");
const { selectOccurrences } = require("../src/xd");

const fs = require('fs')

describe("selectOccurrences", () => {
  const addFunctionHtml = fs.readFileSync("./test/assets/add-function.html");
  let window = new JSDOM(addFunctionHtml).window;

  it("should select all elements with identifier 'a'", () => {
    // when
    const occurrences = selectOccurrences(window, "a")

    // then
    assert.strictEqual(occurrences.length, 2)
  })

  it("should select all elements with identifier 'b'", () => {
    // when
    const occurrences = selectOccurrences(window, "b")

    // then
    assert.strictEqual(occurrences.length, 2)
  })
});
