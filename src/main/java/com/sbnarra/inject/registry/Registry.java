package com.sbnarra.inject.registry;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.aspect.Aspect;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Registry {
    private final List<AnnotationBinding> annotationBindings = new ArrayList<>();
    private final List<Binding<?>> bindings = new ArrayList<>();
     private final List<Aspect> aspects = new ArrayList<>();

     private Registry() {
     }

     public static Registry doRegistrations(List<Registration> registers) throws InjectException {
          Registry registry = new Registry();
          for (Registration registration : registers) {
              registration.setRegistry(registry);
              registration.register();
          }
          return registry;
     }

    public <T> Binding<T> bind(Class<T> tClass) {
        Binding<T> binding = new Binding<>(tClass);
        bindings.add(binding);
        return binding;
    }

    public <T> Binding<T> bind(Type<T> type) {
        Binding<T> binding = new Binding<>(type);
        bindings.add(binding);
        return binding;
    }

    public AnnotationBinding intercept(Class<?> annotationClass) throws InjectException {
         if (!annotationClass.isAnnotation()) {
             throw new InjectException(annotationClass + " is not an annotation");
         }
        AnnotationBinding annotationBinding = new AnnotationBinding(Annotation.class.getClass().cast(annotationClass));
        annotationBindings.add(annotationBinding);
        return annotationBinding;
    }

    public Binding<?> find(Class<?> aClass) {
         for (Binding<?> binding : bindings) {
             Type<?> type = binding.getType();
             if (type.getParameterized() != null && type.getParameterized().getRawType().equals(aClass)) {
                return binding;
             } else if (type.getClazz().getTheClass().equals(aClass)) {
                return binding;
             }
         }
         return null;
    }
}
