package com.sbnarra.inject.graph;

import com.sbnarra.inject.meta.ObjectMeta;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value
public class DependencyNode {
    private final Set<DependencyNode> ancestors = new HashSet<>();
    private final ObjectMeta objectMeta;
    private final Set<DependencyNode> descendants = new HashSet<>();
}
