package com.sbnarra.inject.graph;

import com.sbnarra.inject.meta.ObjectMeta;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class DependencyNode {
    private final List<DependencyNode> ancestors = new ArrayList<>();
    private final List<DependencyNode> descendants = new ArrayList<>();
    private final ObjectMeta objectMeta;
}
