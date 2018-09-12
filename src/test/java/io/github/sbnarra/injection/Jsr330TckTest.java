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


    /*
     * <p>Configure the injector as follows:
     *
     * <ul>
     *   <li>{@link org.atinject.tck.auto.Car} is implemented by
     *       {@link org.atinject.tck.auto.Convertible Convertible}.
     *   <li>{@link org.atinject.tck.auto.Drivers @Drivers}
     *       {@link org.atinject.tck.auto.Seat Seat} is
     *       implemented by {@link org.atinject.tck.auto.DriversSeat DriversSeat}.
     *   <li>{@link org.atinject.tck.auto.Seat Seat} is
     *       implemented by {@link org.atinject.tck.auto.Seat Seat} itself, and
     *       {@link org.atinject.tck.auto.Tire Tire} by
     *       {@link org.atinject.tck.auto.Tire Tire} itself
     *       (not subclasses).
     *   <li>{@link org.atinject.tck.auto.Engine Engine} is implemented by
     *       {@link org.atinject.tck.auto.V8Engine V8Engine}.
     *   <li>{@link javax.inject.Named @Named("spare")}
     *       {@link org.atinject.tck.auto.Tire Tire} is implemented by
     *       {@link org.atinject.tck.auto.accessories.SpareTire SpareTire}.
     *   <li>The following classes may also be injected directly:
     *       {@link org.atinject.tck.auto.accessories.Cupholder Cupholder},
     *       {@link org.atinject.tck.auto.accessories.SpareTire SpareTire}, and
     *       {@link org.atinject.tck.auto.FuelTank FuelTank}.
     * </ul>
     */
    public static Test suite() throws InjectException {
        Injector injector = getInjector();

        return Tck.testsFor(injector.get(Car.class),
                true /* supportsStatic */,
                true /* supportsPrivate */);
    }

    private static Injector getInjector() throws InjectException {
        return InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                bind(Car.class).with(Convertible.class);
                bind(Seat.class).qualified( Drivers.class).with(DriversSeat.class);
                // bind(Seat.class).with(Seat.class);
                // bind(Tire.class).with(Tire.class);
                bind(Engine.class).with(V8Engine.class);
                bind(Tire.class).named("spare").with(SpareTire.class);
            }
        });
    }
}
