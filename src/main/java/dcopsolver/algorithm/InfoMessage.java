package dcopsolver.algorithm;

public class InfoMessage {
    private String name;
    private Integer value;
    private Float cost;
    private Boolean terminated;

    private Integer ubValue;
    private Integer lbValue;
    private Float lb;
    private Float ub;
    private Float threshold;

    public InfoMessage () {}

    public InfoMessage (String name, Integer value, Float cost, Boolean terminated, Float lb, Integer lbValue, Float ub, Integer ubValue, Float threshold) {
        this.name = name;
        this.value = value;
        this.cost = cost;
        this.terminated = terminated;

        this.ubValue = ubValue;
        this.lbValue = lbValue;
        this.lb = lb;
        this.ub = ub;
        this.threshold = threshold;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Integer getValue () {
        return value;
    }

    public void setValue (Integer value) {
        this.value = value;
    }

    public Float getCost () {
        return cost;
    }

    public void setCost (Float cost) {
        this.cost = cost;
    }

    public Boolean getTerminated () {
        return terminated;
    }

    public void setTerminated (Boolean terminated) {
        this.terminated = terminated;
    }

    public Integer getUbValue () {
        return ubValue;
    }

    public void setUbValue (Integer ubValue) {
        this.ubValue = ubValue;
    }

    public Integer getLbValue () {
        return lbValue;
    }

    public void setLbValue (Integer lbValue) {
        this.lbValue = lbValue;
    }

    public Float getLb () {
        return lb;
    }

    public void setLb (Float lb) {
        this.lb = lb;
    }

    public Float getUb () {
        return ub;
    }

    public void setUb (Float ub) {
        this.ub = ub;
    }

    public Float getThreshold () {
        return threshold;
    }

    public void setThreshold (Float threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString () {
        return "InfoMessage{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", cost=" + cost +
                ", terminated=" + terminated +
                ", lb=" + lb +
                "@" + lbValue +
                ", ub=" + ub +
                "@" + ubValue +
                ", threshold=" + threshold +
                '}';
    }
}
