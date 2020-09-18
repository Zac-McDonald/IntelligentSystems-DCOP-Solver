package variable.grabbers;

import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.List;

/**
* this is a service that facilitates a component that gets a list of variables that is being shared on the platform and
* then proposes that it wants to be the owner of one of those variables.
*/
public interface IWantVariable {
  /**
   * it needs to send a future that contains the list of variables being shared on the platform
   * Im not sure if the sender needs to be sent... but it seems like a useful thing
   */
    Future<Void> variables (String sender, List<String> variables);

    /**
     *  Subscribe to the want variable service.
     *  Every couple of seconds, a string with the current variable the agent wants
     *  will be sent to the subscriber.
     */

    public ISubscriptionIntermediateFuture<String> subscribe();

}
