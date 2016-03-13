# pathfinding

Personal project to teach myself pathfinding and stuff

## dependencies
- com.airhacks:afterburner-topgun
- javax.inject:javax.inject
- com.google.guava:guava
- it.unimi.dsi:fastutil
- de.lighti.clipper (http://www.lighti.de/projects/polygon-clipper-for-java/)

## pathfinders

- [A*](https://en.wikipedia.org/wiki/A*_search_algorithm)
- [BFS](https://en.wikipedia.org/wiki/Breadth-first_search)
- [DFS](https://en.wikipedia.org/wiki/Depth-first_search)
- [Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
- [Fringe](https://en.wikipedia.org/wiki/Fringe_search)
- [IDA*](https://en.wikipedia.org/wiki/Iterative_deepening_A*)
- [IDDFS](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search)
- [JPS](https://en.wikipedia.org/wiki/Jump_point_search)
- [Theta](http://aigamedev.com/open/tutorials/theta-star-any-angle-paths/)
- [Lazy Theta](http://aigamedev.com/open/tutorial/lazy-theta-star/)

## searchspaces
- Graph
- Grid
- NavMesh
- QuadTree

## TODO
- QuadTree does not currently permit traveling diagonally between equal sized nodes
- Implement Linear QuadTree instead/also?
- Implement HOT queues?
- Refactor node to not explicitly depend on Vertex?

## Thoughts
- how to handle pathfinder specific rendering needs, such as jps peeked nodes, and hpa abstracted graphs?
- editable and searchable graph maps should probably be separated from the rest. right?
