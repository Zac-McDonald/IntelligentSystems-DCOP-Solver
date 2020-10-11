package chat.testing.chatv3;


import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * A chatting interface used by version one and two of the project.
 */
public interface IChatService {
    //the message function
    Future<Void> message (String sender, Event event);
    //a reference to itself
    IInternalAccess getAgent();
}
