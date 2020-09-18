package chat.testing.chatv2;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import chat.testing.IChatService;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A class that implements the IChatService interface.
 * The @StartService function is executed as soon as the service is started on
 * a component, which happens just after the component is created. In this case
 * the component using this service will look for the clockservice and once
 * the future is returned will bind it ot the clock variable inside of this
 * service. It also initialises the GUI for the component that is running this
 * service.
 */
@Service
public class ChatServiceD2 implements IChatService {
    @ServiceComponent
    IInternalAccess agent;
    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;
    private IClockService clock;
    private DateFormat format;
    private ChatGuiD2 gui;

    /**
     * Initialisation of the service
     * @return Not used
     */
    @ServiceStart
    public IFuture<Void> startService () {
        format = new SimpleDateFormat("hh:mm:ss");
        final Future<Void> ret = new Future<Void>();
        final IExternalAccess exta = agent.getExternalAccess();
        gui = createGui(exta);
        IFuture<IClockService> fut = requiredServicesFeature.getRequiredService("clockservice");
        fut.addResultListener(new SwingExceptionDelegationResultListener<IClockService, Void>(ret) {
            public void customResultAvailable(IClockService result) {
                clock = result;
                ret.setResult(null);
            }
        });
        return ret;
    }

    /**
     * Make sure to correctly close of the Swing thread
     * @return Not used
     */
    @ServiceShutdown
    public IFuture<Void> shutdownService() {
        SwingUtilities.invokeLater(() -> gui.dispose());
        return null;
    }

    /**
     *
     * @param agent The agent that the GUI will interact with.
     * @return The new GUI object
     */
    protected ChatGuiD2 createGui(IExternalAccess agent){
        return new ChatGuiD2(agent);
    }

    /**
     * Puts the message to the user screen
     * @param sender The sending agent
     * @param text The main message
     * @return Not used
     */
    public Future<Void> message(String sender, String text) {
        gui.addMessage(agent.getComponentIdentifier().getName() +
                " received at: " + format.format(clock.getTime()) +
                " from: " + sender +
                "message:" + text);
        return null;
    }
}
