package storm.reflect;

import junit.framework.TestCase;

/**
 * Created by Dimitry Ivanov on 25.01.2016.
 */
public class ReflectionInstanceCreatorTest extends TestCase {

    public static class PublicPublicEmpty {}
    public void testPublicEmpty() {
        assertClass(PublicPublicEmpty.class);
    }


    public static class PublicPrivateEmpty {
        private PublicPrivateEmpty() {}
    }
    public void testPublicPrivateEmpty() {
        assertClass(PublicPrivateEmpty.class);
    }


    public static class PublicPackageEmpty {
        PublicPackageEmpty() {}
    }
    public void testPublicPackageEmpty() {
        assertClass(PublicPackageEmpty.class);
    }


    public static class PublicProtectedEmpty {
        protected PublicProtectedEmpty() {}
    }
    public void testPublicProtectedEmpty() {
        assertClass(PublicProtectedEmpty.class);
    }


    private static class PrivatePublicEmpty {}
    public void testPrivatePublicEmpty() {
        assertClass(PrivatePublicEmpty.class);
    }


    private static class PrivatePackageEmpty {
        PrivatePackageEmpty() {}
    }
    public void testPrivatePackageEmpty() {
        assertClass(PrivatePackageEmpty.class);
    }


    private static class PrivateProtectedEmpty {
        protected PrivateProtectedEmpty() {}
    }
    public void testPrivateProtectedEmpty() {
        assertClass(PrivateProtectedEmpty.class);
    }


    private static class PrivatePrivateEmpty {
        private PrivatePrivateEmpty() {}
    }
    public void testPrivatePrivateEmpty() {
        assertClass(PrivatePrivateEmpty.class);
    }


    public static class PublicPublicNotEmpty {
        final String arg;
        public PublicPublicNotEmpty(String arg1) {
            this.arg = arg1;
        }
    }
    public void testPublicPublicNotEmpty() {

        final PublicPublicNotEmpty first    = assertClass(PublicPublicNotEmpty.class, String.class);
        final PublicPublicNotEmpty second   = assertClass(PublicPublicNotEmpty.class, "");

        if (first != null) {
            assertEquals(null, first.arg);
        }

        if (second != null) {
            assertEquals("", second.arg);
        }
    }

    public static class PublicPrivateNotEmpty {
        final Integer arg1;
        final Long arg2;
        final Double arg3;
        private PublicPrivateNotEmpty(Integer arg1, Long arg2, Double arg3) {
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }
    }
    public void testPublicPrivateNotEmpty() {
        final PublicPrivateNotEmpty first = assertClass(PublicPrivateNotEmpty.class, Integer.class, Long.class, Double.class);
        final PublicPrivateNotEmpty second = assertClass(PublicPrivateNotEmpty.class, 12, 55L, .45D);

        if (first != null) {
            assertEquals(null, first.arg1);
            assertEquals(null, first.arg2);
            assertEquals(null, first.arg3);
        }

        if (second != null) {
            assertEquals(second.arg1, Integer.valueOf(12));
            assertEquals(second.arg2, Long.valueOf(55L));
            assertEquals(second.arg3, Double.valueOf(.45D));
        }
    }

    private <T> T assertClass(Class<?> cl, Object... constructorArgs) {
        try {
            final Object obj = ReflectionInstanceCreator.newInstance(cl, constructorArgs);
            assertTrue(obj != null);
            //noinspection unchecked
            return (T) obj;
        } catch (Throwable t) {
            throw t;
        }
    }
}
