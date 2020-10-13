package message;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;

public interface IMessageService {

    Future<Void> message(Data content);
    IInternalAccess getAgent();
}
