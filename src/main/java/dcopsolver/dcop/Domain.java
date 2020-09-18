package dcopsolver.dcop;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;

public class Domain {
    String name;
    String semanticType;

    Boolean isIntegerDomain;
    ArrayList<Integer> integerValues;
    ArrayList<String> stringValues;

    // Initialiser for Integer domains
    public Domain (String name, String semanticType, Integer[] values) {
        this.name = name;
        this.semanticType = semanticType;

        this.isIntegerDomain = true;
        this.integerValues = new ArrayList<Integer>(Arrays.asList(values));
        this.stringValues = new ArrayList<String>();
    }

    // Initialiser for String domains
    public Domain (String name, String semanticType, String[] values) {
        this.name = name;
        this.semanticType = semanticType;

        this.isIntegerDomain = false;
        this.stringValues = new ArrayList<String>(Arrays.asList(values));;
        this.integerValues = new ArrayList<Integer>();
    }

    public Integer size () {
        return integerValues.size() + stringValues.size();
    }

    public Boolean contains (Integer value) {
        return (isIntegerDomain && integerValues.contains(value));
    }

    public Boolean contains (String value) {
        return (!isIntegerDomain && stringValues.contains(value));
    }

    public Integer indexOf (Integer value) {
        if (isIntegerDomain) {
            return integerValues.indexOf(value);
        } else {
            return -1;
        }
    }

    public Integer indexOf (String value) {
        if (!isIntegerDomain) {
            return stringValues.indexOf(value);
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain domain = (Domain) o;
        return Objects.equals(name, domain.name) &&
                Objects.equals(semanticType, domain.semanticType) &&
                Objects.equals(isIntegerDomain, domain.isIntegerDomain) &&
                Objects.equals(integerValues, domain.integerValues) &&
                Objects.equals(stringValues, domain.stringValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, semanticType, isIntegerDomain, integerValues, stringValues);
    }

    @Override
    public String toString() {
        return "Domain{" +
                "name='" + name + '\'' +
                ", semanticType='" + semanticType + '\'' +
                ", isIntegerDomain=" + isIntegerDomain +
                ", integerValues=" + integerValues +
                ", stringValues=" + stringValues +
                '}';
    }
}
