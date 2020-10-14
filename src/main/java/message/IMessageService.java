package message;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;

public interface IMessageService {
    IInternalAccess getAgent();

    Future<Void> message(Data content);
}
