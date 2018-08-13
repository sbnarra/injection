package com.sbnarra.inject.registry.entry;

import com.sbnarra.inject.registry.Type;
import lombok.Value;

@Value
public class ClassEntry {
    private final Type<?> type;
}
