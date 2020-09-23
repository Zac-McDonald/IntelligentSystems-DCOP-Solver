import com.eclipsesource.v8.V8;

public class J2V8_Test {
    public static void main(String[] args) {
        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntScript(""
                + "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world).length;\n");
        System.out.println(result);
        runtime.release();
    }
}

