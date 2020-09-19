package List.Agents;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.List;


/**
 *  Simple service to publish the local system time.
 *  As the service does not change the local system
 *  and provides no sensitive information, no security
 *  restrictions are required.
 */
@Security(Security.UNRESTRICTED)
public interface IListService
{
    /**
     *  Subscribe to the list service.
     *  Every couple of seconds, a list
     *  will be sent to the subscriber.
     */
    public ISubscriptionIntermediateFuture<List<String>> subscribe();

    /**
     *  provide a list...
     */
    public List<String> list ();
}