import java.io.InputStream;

public class ClassLoaderTest {
    public static void main(String[] args) throws Exception {
        ClassLoader loader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) {
                System.out.println("iiii");
                try {
                    InputStream is = getClass().getResourceAsStream(name + ".class");
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass("Evil", b, 0, b.length);
                } catch (Exception e) {
                    return null;
                }
            }
        };
        Object obj = loader.loadClass("Evil").newInstance();
        Evil evil = new Evil();
        //obj = loader.loadClass("Evil").newInstance();
        //System.out.println(obj instanceof Evil);
    }
}
