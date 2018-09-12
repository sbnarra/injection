package io.github.sbnarra.injection;

import io.github.sbnarra.injection.core.Debug;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) throws InjectException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        SpareTire spareTire = new SpareTire(new FuelTank(), new FuelTank());

        Method tireMethod = Tire.class.getDeclaredMethod("injectPackagePrivateMethod");
        tireMethod.setAccessible(true);
        tireMethod.invoke(spareTire);
        Method spareTireMethod = SpareTire.class.getDeclaredMethod("injectPackagePrivateMethod");
        spareTireMethod.setAccessible(true);
        spareTireMethod.invoke(spareTire);

        Field superField = Tire.class.getDeclaredField("superPackagePrivateMethodInjected");
        superField.setAccessible(true);
        Field subTireField = Tire.class.getDeclaredField("subPackagePrivateMethodInjected");
        subTireField.setAccessible(true);

        Debug.log(superField.get(spareTire));
        Debug.log(subTireField.get(spareTire));

//        Injector injector = getInjector();
    }
    public static class AA {
        @Inject
        public void aa() {}
    }

    public static class BB extends AA {
        @Override
        public void aa() {}
    }
}
