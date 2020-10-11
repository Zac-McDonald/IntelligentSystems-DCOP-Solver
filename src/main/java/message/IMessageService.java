package message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import messagequeue.Event;

public interface IMessageService {

//    Future<Void> message(Data content);
    //other agents open the line of communication
    public ISubscriptionIntermediateFuture<Data> subscribe(IComponentIdentifier agentID);
    //a reference to itself
    IInternalAccess getAgent();
}
