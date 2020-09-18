package nQueensSolver;

import jadex.bdiv3.annotation.Body;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Uses its input arguments to ask about the chess board and prints it.
 * Most code taken from: ArgumentsAgent and F1Agent.
 */
@Agent
@Arguments(@Argument(name="nAsk", description = "Number of queens this agent should ask for",
        clazz=Integer.class, defaultvalue = "5"))
@RequiredServices(@RequiredService(name="nQueens", type= IQueens.class,
        binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)))
public class AskAgent {
    @AgentArgument
    Integer nAsk;
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    /**
     * @param agent The agent access used to get the agents name.
     */
    @AgentBody
    public void body (IInternalAccess agent) {
        IFuture<IQueens> fut = requiredServicesFeature.getRequiredService("nQueens");
        // -- Using a listener for the service result.
        fut.addResultListener(new DefaultResultListener<IQueens>() {
            @Override
            public void resultAvailable(IQueens iQueens) {
                iQueens.solveNQueens(nAsk)
                        .addResultListener(l -> prettyPrintResults(l,
                                agent.getComponentIdentifier().getLocalName()));
            }
        });
    }
    /**
     * A helper to make the output look nice
     * @param l The list of integer results
     * @param name The name of this agent.
     */
    private void prettyPrintResults (List<Integer> l, String name) {
        if (l.size() < 1) {
            System.out.println("-----------------------------------\n " +
                    "Hey, something went wrong for " + name);
            return;
        }
        System.out.println( "-----------------------------------\n" +
                name + " got the solution to the "
                + nAsk + " queens problem::");
        // Or, for single line formatting using StreamAPI
        System.out.println(
            IntStream.range(0, l.size())
                    .mapToObj(i -> "Q" + i + " := " + l.get(i))
                    .collect(Collectors.joining(" :: ")));
    }

}
