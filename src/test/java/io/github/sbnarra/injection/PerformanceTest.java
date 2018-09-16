package io.github.sbnarra.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.RegistryException;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.SpareTire;
import org.junit.jupiter.api.Test;

public class PerformanceTest {

    private static final int count = 200;

    @Test
    public void injectorTest() throws InjectException {
        for (int i = 0; i < count; i++) {
            Injector injector = InjectorFactory.create(new Registration() {
                @Override
                public void register() throws RegistryException {
                    bind(Car.class).with(Convertible.class);
                    bind(Engine.class).with(V8Engine.class);
                    bind(Tire.class).named("spare").with(SpareTire.class);
                    bind(Seat.class).qualified( Drivers.class).with(DriversSeat.class);
                }
            });
            injector.get(Car.class);
        }
    }

    @Test
    public void guiceInjectorTest() {
        for (int i = 0; i < count; i++) {
            com.google.inject.Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                public void configure() {
                    bind(Car.class).to(Convertible.class);
                    bind(Engine.class).to(V8Engine.class);
                    bind(Tire.class).annotatedWith(Names.named("spare")).to(SpareTire.class);
                    bind(Seat.class).annotatedWith( Drivers.class).to(DriversSeat.class);
                }
            });
            injector.getInstance(Car.class);
        }
    }
}
