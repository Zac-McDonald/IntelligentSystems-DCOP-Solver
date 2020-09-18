package variable.grabbers;

import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.List;

// this service is for when a  component has secured a variable and will broadcast that it has found one.

public interface IHaveVariable {
    /**
     * Sends the variable that it owns to all its subscribers?
     * */

    Future<Void> variable_owned (String sender, String variable);

    /**
     *  Subscribe to the want variable service.
     *  Every couple of seconds, a string with the current variable the agent wants
     *  will be sent to the subscriber.
     */

    public ISubscriptionIntermediateFuture<String> subscribe();
}
