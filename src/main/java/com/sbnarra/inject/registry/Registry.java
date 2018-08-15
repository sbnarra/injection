package com.sbnarra.inject.registry;

import com.sbnarra.inject.aspect.Aspect;

import com.sbnarra.inject.registry.binding.Binding;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class Registry {
     private final List<Binding> bindings = new ArrayList<>();
     private final List<Aspect> aspects = new ArrayList<>();
}
