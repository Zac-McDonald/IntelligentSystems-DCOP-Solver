package dcopsolver.algorithm;

import dcopsolver.dcop.Variable;

import java.util.HashMap;

public class TerminateMessage {
    private HashMap<String, Integer> context;

    public TerminateMessage () {}

    public TerminateMessage (HashMap<String, Integer> context) {
        this.context = new HashMap<>(context);
    }

    public HashMap<String, Integer> getContext () {
        return context;
    }

    public void setContext (HashMap<String, Integer> context) {
        this.context = context;
    }

    @Override
    public String toString () {
        return "TerminateMessage{ context = " + context.toString() + " }";
    }
}
