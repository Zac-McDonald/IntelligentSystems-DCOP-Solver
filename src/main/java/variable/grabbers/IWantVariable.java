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
   * agent is sends a request for a variable with its name as a part of the request
   */

    Future<Void> negotiate (String sender, String want);




}
