package dcopsolver.algorithm;

public class InfoMessage {
    private String name;
    private Integer value;
    private Float cost;
    private Boolean terminated;

    public InfoMessage () {}

    public InfoMessage (String name, Integer value, Float cost, Boolean terminated) {
        this.name = name;
        this.value = value;
        this.cost = cost;
        this.terminated = terminated;
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

    @Override
    public String toString () {
        return "InfoMessage{" +
                "name=" + name +
                ", value=" + value +
                ", cost=" + cost +
                ", terminated=" + terminated +
                '}';
    }
}
