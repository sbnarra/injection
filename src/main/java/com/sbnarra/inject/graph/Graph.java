package com.sbnarra.inject.graph;

import com.sbnarra.inject.registry.Register;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Graph {

    private final List<Node> roots;

    public Graph construct(Register register) {
        Graph graph = new Graph(new ArrayList<>());

        return graph;
    }
}
