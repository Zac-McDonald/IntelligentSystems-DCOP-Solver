package variable.grabbers;

import chat.testing.chatv2.ChatGuiD2;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.text.DateFormat;
import java.util.List;

/**
 * Implimentation of the IWantVariable service.
 * */
@Service
public class WantService implements IWantVariable, IHaveList {
    @ServiceComponent
    IInternalAccess agent; //reference to itself
    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;

    @Override
    public Future<Void> negotiate(String sender, String want) {
        return null;
    }

    @Override
    public Future<Void> sendVars(List<String> variables) {
        return null;
    }
}
