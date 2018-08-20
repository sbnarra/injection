## flow

### injector creation

1. registration process(user implements modules) - bind entries to a registry using the register class
1. graphing dependencies(user requests injector using modules) - use the registers to build a dependency graph containing meta data

### instance request

1. use resolver to find meta within graph
1. use object creator to build

To-Do...
 * scoping - almost complete
 * interfacing - double check
 * provider/provides
 * Coverage (coveralls)
 * CI (circleci)
 * README.md
 * gh-pages
 
 * central repo hosting?