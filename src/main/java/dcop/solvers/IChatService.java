package dcop.solvers;


import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;

/**
 * A chatting interface used by version one and two of the project.
 */
public interface IChatService {
    Future<Void> message (String sender, Event event);
    IInternalAccess getAgent();
}
