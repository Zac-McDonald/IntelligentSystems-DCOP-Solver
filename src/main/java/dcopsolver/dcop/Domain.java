package dcopsolver.dcop;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class Domain {
    String name;
    String semanticType;
    Set<Integer> values;

    public Domain (String name, String semanticType, Set<Integer> values) {
        this.name = name;
        this.semanticType = semanticType;
        this.values = values;
    }

    public Integer size () {
        return values.size();
    }

    public Boolean contains (Integer value) {
        return values.contains(value);
    }

    public Iterator<Integer> iterator () {
        return values.iterator();
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain domain = (Domain) o;
        return Objects.equals(name, domain.name) &&
                Objects.equals(semanticType, domain.semanticType) &&
                Objects.equals(values, domain.values);
    }

    @Override
    public int hashCode () {
        return Objects.hash(name, semanticType, values);
    }

    @Override
    public String toString () {
        return "Domain{" +
                "name='" + name + '\'' +
                ", semanticType='" + semanticType + '\'' +
                ", values=" + values +
                '}';
    }
}
