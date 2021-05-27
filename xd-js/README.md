# xd-js

The **xd-js** module is responsible for user interaction in the XD feature.

## Development

### Tests

```shell
./gradlew testJs
```

### Lint

#### Check

```shell
./gradlew eslintCheck
```

#### Fix

```shell
./gradlew eslintFix
```

### Quirks

#### 1. npm install

Executing `npm install` on the command line upgrades `package-lock.json` to version 2. So please use,

```shell
./gradlew npmInstall
```

#### 2. Approvals on Windows

[Approvals.NodeJS](https://github.com/approvals/Approvals.NodeJS) pulls in Window's specific dependencies after
a `npm install`. The `install` command makes changes to `package-lock.json` which will be reversed when building on
non-Windows environments. There is an unresolved [issue](https://github.com/approvals/Approvals.NodeJS/issues/111)
regarding the same.

Until the above mentioned issue is resolved, please run the following command before building the project on Windows.

```shell
git update-index --assume-unchanged xd-js/package-lock.json
```

In case, if you want to make changes to the file, you can reverse the effect using,

```shell
git update-index --no-assume-unchanged xd-js/package-lock.json
```

#### 3. Approval tests

Approvals.NodeJS requires the title of the test to create matching approved/received files. Mocha has a `test` property
that provides this information.

```javascript
describe('group name', () => {
  it('test title', /* ✅ */ function () {
    const testTitle = this.test.title
    // ...
  });
});
```

However, `test` object cannot be accessed using the arrow syntax. Therefore, all approval tests should use the anonymous
function syntax.

```javascript
describe('group name', () => {
  it('test title', /* ❌ */ () => {
    const testTitle = this.test.title // `test` will be `undefined` 😔
    // ...
  });
});
```

## License

```
Copyright 2021 Red Green, Inc.
```
