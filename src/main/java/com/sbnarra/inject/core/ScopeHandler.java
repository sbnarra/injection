package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.Meta;

public interface ScopeHandler {
    <T> T get(Meta<T> meta, Context context) throws InjectException;
}
