import com.eclipsesource.v8.V8;

public class J2V8_Test {
    public static void main(String[] args) {
        V8 runtime = V8.createV8Runtime();

        int result = runtime.executeIntegerScript(""
                + "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world).length;\n");
        System.out.println(result);

        String fnc = "(v0 + v2) * v1";
        String assign = "v0=2;v1=3;v2=0;";
        double fncResult = runtime.executeDoubleScript(assign + fnc);
        System.out.println(fncResult);

        runtime.release();
    }
}

