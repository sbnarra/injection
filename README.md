master
* Travis-CI: [![Build Status](https://travis-ci.org/sbnarra/injection.svg?branch=master)](https://travis-ci.org/sbnarra/injection)
* Coveralls: [![Coverage Status](https://coveralls.io/repos/github/sbnarra/injection/badge.svg?branch=master)](https://coveralls.io/github/sbnarra/injection?branch=master)

initial WIP
* Travis-CI: [![Build Status](https://travis-ci.org/sbnarra/injection.svg?branch=initial_wip)](https://travis-ci.org/sbnarra/injection)
* Coveralls: [![Coverage Status](https://coveralls.io/repos/github/sbnarra/injection/badge.svg?branch=initial_wip)](https://coveralls.io/github/sbnarra/injection?branch=initial_wip)

## flow

### injector creation

1. registration process(user implements modules) - bind entries to a registry using the register class
1. graphing dependencies(user requests injector using modules) - use the registers to build a dependency graph containing meta data

### instance request

1. use resolver to find meta within graph
1. use object creator to build

To-Do...
 * CI (travisci)
 * Coverage (coveralls)
 * provider/provides
 * README.md
 * gh-pages
 * maven central