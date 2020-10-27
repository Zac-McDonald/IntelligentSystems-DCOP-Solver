package message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;

public interface IMessageService {
    IInternalAccess getAgent();
    IComponentIdentifier getId();
    Future<Void> message(Data content);
}
