package io.github.sbnarra.injection;

import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.RegistryException;
import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.SpareTire;

public class Jsr330TckTest {
    public static Test suite() throws InjectException {
        return Tck.testsFor(InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                bind(Car.class).with(Convertible.class);
                bind(Engine.class).with(V8Engine.class);
                bind(Tire.class).named("spare").with(SpareTire.class);
                bind(Seat.class).qualified( Drivers.class).with(DriversSeat.class);
            }
        }).get(Car.class), true, true);
    }
}
