package io.github.sbnarra.injection.meta;

import io.github.sbnarra.injection.type.Type;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.util.List;

@Value
@Builder
public class Meta<T> {

    private final T instance;

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final List<Members> members;
    private final List<Aspect> aspect;

    @Value
    @Builder
    public static class Inject {
        private final Annotation scoped;
        private final Annotation qualifier;
    }

    @Value
    @Builder
    public static class Class<T> {
        @NonNull private final Inject inject;
        private final java.lang.Class<? extends T> buildClass;
        private final java.lang.Class<?> contractClass;
        private final java.lang.Class<?> bindClass;
    }


    @Value
    @Builder
    public static class Constructor<T> {
        private final java.lang.reflect.Constructor<? extends T> constructor;
        private final List<Parameter> parameters;
    }

    @Value
    @Builder
    public static class Members {
        private final java.lang.Class<?> theClass;
        private final List<Field> fields;
        private final List<Method> methods;
    }

    @Value
    @Builder
    public static class Field {
        private final java.lang.reflect.Field field;
        private final Parameter parameter;
    }


    @Value
    @Builder
    public static class Method {
        private final java.lang.reflect.Method method;
        private final List<Parameter> parameters;
    }


    @ToString
    @EqualsAndHashCode
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Parameter {
        private final Inject inject;
    }

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class InstanceParameter extends Parameter {
        private final Meta<?> meta;

        private InstanceParameter(Meta<?> meta, Inject inject) {
            super(inject);
            this.meta = meta;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Meta<?> meta;
            private Inject inject;

            public Builder meta(Meta<?> meta) {
                this.meta = meta;
                return this;
            }

            public Builder inject(Inject inject) {
                this.inject = inject;
                return this;
            }

            public InstanceParameter build() {
                return new InstanceParameter(meta, inject);
            }
        }
    }

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class ProviderParameter<T> extends Parameter {
        private final Type<T> type;

        private ProviderParameter(Type<T> type, Inject inject) {
            super(inject);
            this.type = type;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder<T> {
            private Type<T> type;
            private Inject inject;

            public Builder type(Type<T> type) {
                this.type = type;
                return this;
            }

            public Builder inject(Inject inject) {
                this.inject = inject;
                return this;
            }

            public ProviderParameter build() {
                return new ProviderParameter(type, inject);
            }
        }
    }

    @Value
    @Builder
    public static class Aspect {
        private final java.lang.Class<? extends Annotation> annotationClass;
        private final io.github.sbnarra.injection.aspect.Aspect aspect;
    }
}
