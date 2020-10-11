package chat.testing.chatv3;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A class that implements the IChatService interface.
 * The @StartService function is executed as soon as the service is started on
 * a component, which happens just after the component is created. In this case
 * the component using this service will look for the clockservice and once
 * the future is returned will bind it ot the clock variable inside of this
 * service.
 */
@Service
public class ChatServiceD1 implements IChatService {
    @ServiceComponent
    IInternalAccess agent;
    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;
    @ServiceComponent

    private IClockService clock;
    private final DateFormat format = new SimpleDateFormat("hh:mm:ss");

    /**
     * Makes sure the required variables are correctly initialised.
     * @return Not used
     */
    @ServiceStart
    public IFuture<Void> startService () {
        final Future<Void> ret = new Future<Void>();
        IFuture<IClockService> fut = requiredServicesFeature.getRequiredService("clockservice");
        fut.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret) {
            public void customResultAvailable(IClockService result) {
                clock = result;
                ret.setResult(null);
            }
        });
        return ret;
    }

    /**
     *
     * @param sender The senders name.
     * @param event The data transfered
     * @return Not used
     */
    public Future<Void> message(String sender, Event event) {
        //print basic info
        //currently sending the sender twice
        System.out.println(agent.getComponentIdentifier().getName() +
                " received at: " + format.format(clock.getTime()) +
                " from: " + sender +
                " event:" + event.toString());
        return null;
    }

    public IInternalAccess getAgent(){
        return agent;
    }


}
