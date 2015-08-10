package org.nekosaur.pathfinding.gui.business;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<A,B,C> {

    public void accept(A a, B b, C c);

    public default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
        	accept(a, b, c);
        	after.accept(a, b, c);
        };
    }
}