package dcopsolver.algorithm;

import java.util.HashMap;

public class ThresholdMessage {
    private Float thresh;
    private HashMap<String, Integer> context;

    public ThresholdMessage () {}

    public ThresholdMessage (Float thresh, HashMap<String, Integer> context) {
        this.thresh = thresh;
        this.context = new HashMap<>(context);
    }

    public Float getThresh () {
        return thresh;
    }

    public void setThresh (Float thresh) {
        this.thresh = thresh;
    }

    public HashMap<String, Integer> getContext () {
        return context;
    }

    public void setContext (HashMap<String, Integer> context) {
        this.context = context;
    }

    @Override
    public String toString () {
        return "ThresholdMessage{ t = " + thresh.toString() +
                ", context = " + context.toString() +
                " }";
    }
}
