package io.github.sbnarra.injection.graph;

import io.github.sbnarra.injection.meta.Meta;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
public class Node<T> {
    private final Set<Node<?>> ancestors = new HashSet<>();
    private final Meta<T> meta;
    private final Set<Node<?>> descendants = new HashSet<>();
}