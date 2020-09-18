package service.testing;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 *  This service is the WIP Decop service that
 *  each agent has and is able to call on other Decop Components on the network.
 *  It is currently without any security but will probably need one eventually.
 */
@Security(Security.UNRESTRICTED)
public interface IDecopService
{
    /**
     *  Subscribe to the Decop service.
     *  Currently every couple of seconds,
     *  a placeholder string will be sent to the subscriber.
     */
    public ISubscriptionIntermediateFuture<String>  subscribe();

    /**
     *  Get the location of the platform, where the Decop service runs.
     *  The location is a constant value for each service, therefore it can be cached
     *  and no future is needed. (Thanks Charles)
     */
    public String   getLocation();
}