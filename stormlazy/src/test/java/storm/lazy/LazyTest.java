package storm.lazy;

import junit.framework.TestCase;

/**
 * Created by Dimitry Ivanov on 30.11.2015.
 */
public class LazyTest extends TestCase {

    public void testProviderCalled() {
        final Lazy<Void> lazy = new Lazy<>(new Lazy.LazyProvider<Void>() {
            @Override
            public Void provide() {
                return null;
            }
        });
        lazy.get();
        assertTrue(lazy.isProviderCalled());
    }

    public void testValueTheSame() {
        final Lazy<Object> lazy = new Lazy<>(new Lazy.LazyProvider<Object>() {
            @Override
            public Object provide() {
                return new Object();
            }
        });

        final Object first = lazy.get();
        for (int i = 0; i < 5; i++) {
            assertTrue(first == lazy.get());
        }
    }
}
