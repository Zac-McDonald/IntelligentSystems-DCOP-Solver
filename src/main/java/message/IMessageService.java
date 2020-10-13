package message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import messagequeue.Event;

public interface IMessageService {

    Future<Void> message(Data content);
    IInternalAccess getAgent();
}
