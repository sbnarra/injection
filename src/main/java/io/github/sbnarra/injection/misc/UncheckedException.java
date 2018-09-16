package io.github.sbnarra.injection.misc;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UncheckedException extends Exception {

    protected UncheckedException(String msg, Exception e) {
        super(msg, e);
    }

    protected UncheckedException(String msg) {
        super(msg);
    }

    public Unchecked unchecked() {
        return new Unchecked(this);
    }

    public static <T, E extends UncheckedException> void checkedForEach(List<T> list, Consumer<T> consumer, Class<E> exception) throws E {
        checkedForEach(list.stream(), consumer, exception);
    }

    public static <T, E extends UncheckedException> void checkedParallelForEach(List<T> list, Consumer<T> consumer, Class<E> exception) throws E {
        checkedForEach(list.parallelStream(), consumer, exception);
    }

    public static <T, E extends UncheckedException> void checkedForEach(Stream<T> stream, Consumer<T> consumer, Class<E> exception) throws E {
        try {
            stream.forEach(item -> consumer.accept(item));
        } catch (UncheckedException.Unchecked e) {
            throw e.checked(exception);
        }
    }

    public class Unchecked extends RuntimeException {
        private UncheckedException exception;

        private Unchecked(UncheckedException exception) {
            super(exception);
            this.exception = exception;
        }


        public <T extends UncheckedException> T checked(Class<T> exceptionClass) {
            return exceptionClass.cast(exception);
        }
    }
}
