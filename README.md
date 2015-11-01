# pathfinding

trying for a refactor of pfsandbox

## dependencies
 - com.airhacks:afterburner-topgun
 - javax.inject:javax.inject
 - com.google.guava:guava
 - it.unimi.dsi:fastutil

## pathfinders

- ported most of the pathfinders from pfsandbox
- fringe search needs to be checked
- go through all and optimize where possible

## searchspaces
- implement more. look at corner graphs, nav meshes?
- quadtree does not currently permit traveling diagonally between equal sized nodes

## datastructures
- HOT queues?

## thoughts
- probably not viable, but having map and vertex be completely immutable, pathfinders keep track of local variables by themselves, use functional interfaces?
- how to handle pathfinder specific rendering needs, such as jps peeked nodes, and hpa abstracted graphs?
- editable and searchable graph maps should probably be separated from the rest. right?
