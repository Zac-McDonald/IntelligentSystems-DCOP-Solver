package dcopsolver.algorithm;

import dcopsolver.dcop.Variable;

import java.util.HashMap;

public class ThresholdMessage {
    private Float t;
    private HashMap<String, Integer> context;

    public ThresholdMessage (Float t, HashMap<String, Integer> context) {
        this.t = t;
        this.context = context;
    }

    public Float getT () {
        return t;
    }

    public void setT (Float t) {
        this.t = t;
    }

    public HashMap<String, Integer> getContext () {
        return context;
    }

    public void setContext (HashMap<String, Integer> context) {
        this.context = context;
    }

    @Override
    public String toString () {
        return "ThresholdMessage{ t = " + t.toString() +
                ", context = " + context.toString() +
                " }";
    }
}
