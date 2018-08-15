package com.sbnarra.inject.registry;

import com.sbnarra.inject.registry.binding.ClassBinding;
import com.sbnarra.inject.registry.binding.TypeBinding;

public interface Registration {

    ClassBinding bind(Class<?> theClass);

    TypeBinding bind(Type<?> theType);
}
