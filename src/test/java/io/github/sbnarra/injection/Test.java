package io.github.sbnarra.injection;

import io.github.sbnarra.injection.core.Debug;
import io.github.sbnarra.injection.core.Type;
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
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Provider;

public class Test {

    public static void main(String[] args) throws InjectException {
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                bind(Car.class).with(Convertible.class);
                bind(Engine.class).with(V8Engine.class);
                bind(Tire.class).named("spare").with(SpareTire.class);
                bind(Seat.class).qualified( Drivers.class).with(DriversSeat.class);
            }
        });

        Cupholder one = injector.get(Seat.class).getCupholder();
        Cupholder two = injector.get(Cupholder.class);

        Cupholder t = injector.get(new Type<Provider<Cupholder>>() {}).get();
        Cupholder aa= injector.get(new Type<Provider<Seat>>() {}).get().getCupholder();

        Debug.log(one == two);
        Debug.log(t == aa);
    }
}
