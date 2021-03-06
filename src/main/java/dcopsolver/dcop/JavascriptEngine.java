package dcopsolver.dcop;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8ResultUndefined;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Pattern;

public class JavascriptEngine {
    public static final Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{Nl}$_][\\p{L}\\p{Nl}$\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}]*");

    // TODO: Investigate ways to not NEED to make a new runtime per request
    //       The runtime can only be run in the thread it is created, when we initialise
    //       the agent, the runtime is created in that thread and functions here are on
    //       the agents thread, or wherever tf JadeX put it.
    //       See: https://github.com/eclipsesource/J2V8/issues/330
    // TODO: Consider not using JadeX and just Singleton the JavascriptAgent/Engine
    //       Means we can use it in regular Java and we still have one per platform
    //       Will probably need to implement/use a mutex on the runtime though
    private static JavascriptEngine instance = null;
    private String libraryPath;

    private JavascriptEngine () {
        this.libraryPath = null;
        //
    }

    public static JavascriptEngine getInstance() {
        if (instance == null) {
            synchronized (JavascriptEngine.class) {
                if (instance == null) {
                    instance = new JavascriptEngine();
                }
            }
        } else if (instance.libraryPath == null) {
            System.err.println("Warning: JavascriptEngine libraryPath not set, J2V8 will crash with multiple JVMs");
        }
        return instance;
    }

    public static void setupEngine (String libraryPath) {
        getInstance().libraryPath = Paths.get(libraryPath).toAbsolutePath().toString();
        try {
            Files.createDirectories(Paths.get(libraryPath));
        } catch (IOException e) {
            System.err.println("Failed to create/open JavascriptEngine library path: \n\t" + e.toString());
        }
    }

    public static String getAssignment (HashMap<String, Integer> variableAssignments) {
        StringBuilder sb = new StringBuilder();
        for (String v : variableAssignments.keySet()) {
            sb.append(v).append("=").append(variableAssignments.get(v)).append(";");
        }
        return sb.toString();
    }

    public Float evaluateFloatExpression (String expression, String sources) {
        V8 runtime = V8.createV8Runtime(null, libraryPath);

        // Execute sources first - if they exist
        if (!sources.isEmpty()) {
            runtime.executeVoidScript(sources);
        }

        Float result = ((Double)runtime.executeDoubleScript(expression)).floatValue();

        runtime.release();
        return result;
    }

    public Float evaluateFloatExpression (String expression) {
        return evaluateFloatExpression(expression, "");
    }

    public Boolean validFloatExpression (String expression, String sources) {
        V8 runtime = V8.createV8Runtime(null, libraryPath);
        boolean result = true;

        try {
            // Execute sources first - if they exist
            if (!sources.isEmpty()) {
                runtime.executeVoidScript(sources);
            }

            Object obj = runtime.executeDoubleScript(expression);
        } catch (V8ResultUndefined e) {
            result = false;
        }

        runtime.release();
        return result;
    }

    public Boolean validFloatExpression (String expression) {
        return validFloatExpression(expression, "");
    }
}
