# pathfinding

personal project to teach myself pathfinding and everything it entails

## dependencies
- com.airhacks:afterburner-topgun
- javax.inject:javax.inject
- com.google.guava:guava
- it.unimi.dsi:fastutil
- de.lighti.clipper (http://www.lighti.de/projects/polygon-clipper-for-java/)

## pathfinders

- A*
- BFS
- DFS
- Dijkstra
- Fringe
- IDA*
- IDDFS
- JPS
- Theta
- Lazy Theta

## searchspaces
- Graph (not implemented in GUI)
- Grid
- NavMesh (needs lots of work)
- QuadTree

## TODO
- QuadTree does not currently permit traveling diagonally between equal sized nodes
- Implement HOT queues?
- Refactor node to not explicitly depend on Vertex?
- Double variant of Vertex?

## thoughts
- how to handle pathfinder specific rendering needs, such as jps peeked nodes, and hpa abstracted graphs?
- editable and searchable graph maps should probably be separated from the rest. right?
