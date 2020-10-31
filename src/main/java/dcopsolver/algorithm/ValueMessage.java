package dcopsolver.algorithm;

import dcopsolver.dcop.Variable;

public class ValueMessage {
    private String other;
    private Integer otherValue;

    public ValueMessage (String other, Integer otherValue) {
        this.other = other;
        this.otherValue = otherValue;
    }

    public String getOther () {
        return other;
    }

    public void setOther (String other) {
        this.other = other;
    }

    public Integer getOtherValue () {
        return otherValue;
    }

    public void setOtherValue (Integer otherValue) {
        this.otherValue = otherValue;
    }

    @Override
    public String toString () {
        return "ValueMessage{ other = " + other.toString() +
                ", otherValue = " + otherValue.toString() +
                " }";
    }
}
