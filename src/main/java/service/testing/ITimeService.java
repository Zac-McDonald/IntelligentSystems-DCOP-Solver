package service.testing;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 *  Simple service to publish the local system time.
 *  As the service does not change the local system
 *  and provides no sensitive information, no security
 *  restrictions are required.
 */
@Security(Security.UNRESTRICTED)
public interface ITimeService
{
    /**
     *  Subscribe to the time service.
     *  Every couple of seconds, a string with the current time
     *  will be sent to the subscriber.
     */
    public ISubscriptionIntermediateFuture<String>  subscribe();

    /**
     *  Get the location of the platform, where the time service runs.
     *  The location is a constant value for each service, therefore it can be cached
     *  and no future is needed.
     */
    public String   getLocation();
}