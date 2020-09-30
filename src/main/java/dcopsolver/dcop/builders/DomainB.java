package dcopsolver.dcop.builders;

import dcopsolver.dcop.Domain;

import java.util.Set;

//Domain Builder
public class DomainB {
    String name;
    String semanticType;
    Set<Integer> values;

    public DomainB() { }

    //creates domain object
    public Domain build() {
        return new Domain(name, semanticType, values);
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setSemanticType(String semanticType) {
        this.semanticType = semanticType;
    }

    public void setValues(Set<Integer> values) {

        this.values = values;
    }
}
