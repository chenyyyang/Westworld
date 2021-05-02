package real.world.tools;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class CasUtil {

    private volatile int value;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    public static CasUtil obj = new CasUtil();

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            valueOffset = unsafe.objectFieldOffset
                    (CasUtil.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static boolean tryOnce() {
        return unsafe.compareAndSwapInt(obj, valueOffset, 0, 1);
    }

    public static boolean reset() {
        return unsafe.compareAndSwapInt(obj, valueOffset, 1, 0);
    }

}
