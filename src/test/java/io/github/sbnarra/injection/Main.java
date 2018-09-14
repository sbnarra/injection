package io.github.sbnarra.injection;

import io.github.sbnarra.injection.core.Debug;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.accessories.SpareTire;

public class Main {
    public static void main(String[] args) throws InjectException {
        Debug.log(SpareTire.class.getDeclaredFields());
        Jsr330TckTest.getInjector().get(Car.class);
    }
}
