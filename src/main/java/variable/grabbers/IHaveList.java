package variable.grabbers;
import jadex.commons.future.Future;
import java.util.List;

/**
 * This Interface facilitates a service of having a list of variables that gets shared around
 * a bunch of agents before they all try to assign a variable to themselves...
 * */
public interface IHaveList {
    Future<Void> sendVars (List<String> variables);
}
