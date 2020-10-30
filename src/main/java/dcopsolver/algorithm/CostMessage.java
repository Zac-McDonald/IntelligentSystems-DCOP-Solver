package dcopsolver.algorithm;

import dcopsolver.dcop.Variable;

import java.util.HashMap;

public class CostMessage {
    private String other;
    private HashMap<String, Integer> otherContext;
    private float otherLB;
    private float otherUB;

    public CostMessage (String other, HashMap<String, Integer> otherContext, float otherLB, float otherUB) {
        this.other = other;
        this.otherContext = otherContext;
        this.otherLB = otherLB;
        this.otherUB = otherUB;
    }

    public String getOther () {
        return other;
    }

    public void setOther (String other) {
        this.other = other;
    }

    public HashMap<String, Integer> getOtherContext () {
        return otherContext;
    }

    public void setOtherContext (HashMap<String, Integer> otherContext) {
        this.otherContext = otherContext;
    }

    public float getOtherLB () {
        return otherLB;
    }

    public void setOtherLB (float otherLB) {
        this.otherLB = otherLB;
    }

    public float getOtherUB () {
        return otherUB;
    }

    public void setOtherUB (float otherUB) {
        this.otherUB = otherUB;
    }

    @Override
    public String toString () {
        return "CostMessage{ other = " + other.toString() +
                ", otherContext = " + otherContext.toString() +
                ", otherLB = " + otherLB +
                ", otherUB = " + otherUB +
                " }";
    }
}
