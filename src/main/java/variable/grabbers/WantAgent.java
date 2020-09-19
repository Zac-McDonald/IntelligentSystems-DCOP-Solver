package variable.grabbers;


import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

@Agent
//The agent both requires and provides the want have services implimented
@RequiredServices({
        @RequiredService(
                name = "wantservice",
                type = IWantVariable.class,
                multiple = true,
                binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true)),
        @RequiredService(
                name = "havelist",
                type = IHaveList.class,
                multiple = true,
                binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true)),})

@ProvidedServices({
        @ProvidedService(
                type = IWantVariable.class,
                implementation = @Implementation(WantService.class)),
        @ProvidedService(
                type = IHaveList.class,
                implementation = @Implementation(WantService.class))})

//a list of strings can be passed into the agent when it is created
@Arguments(@Argument(name = "listOfVariables", description = "the list of variables to distribute", clazz = List.class, defaultvalue = "null"))
public class WantAgent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;
    //where does the agent first get the list from... probably it is an optional launch param.
    List<String> variables;
    String variableWanted;


    /**
     * Sends a want message to each agent providing the IWantVariable (which
     * includes this agent). it then either sends a message containing its name and the variable it wants,
     * or it send the list of variables
     * TODO don't send the message to itself
     * @param agent A reference to the agent (component) that is deployed.
     */
    @AgentBody
    public void body(IInternalAccess agent) {
        if (variables !=null){
            ITerminableIntermediateFuture<IWantVariable> fut = requiredServicesFeature
                    .getRequiredServices("wantservice");
            fut.get()
                    .forEach((it) ->
                            it.negotiate(agent.getComponentIdentifier().getName(), returnWantedVariable()));

        }else{
            ITerminableIntermediateFuture<IWantVariable> fut = requiredServicesFeature
                    .getRequiredServices("haveservice");
            fut.get()
                    .forEach((it) ->
                            it.negotiate(agent.getComponentIdentifier().getName(), returnWantedVariable()));

        }


    }

    public String returnWantedVariable() {
        //TODO impliment a priority system for the variables to get chosen from the list.
        String Placeholder = "PLACEHOLDER STRING";
        if (variables == null) {
            return Placeholder;
        }else{
            return variables.get(0);
        }
    }

}



