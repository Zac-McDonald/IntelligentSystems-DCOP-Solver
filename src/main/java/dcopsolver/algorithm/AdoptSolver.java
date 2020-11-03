package dcopsolver.algorithm;

import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.Variable;
import message.Data;
import message.SolverAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdoptSolver {
    private final Variable assignedVariable;
    private final List<String> allChildren;
    private final List<String> directChildren;
    private final String parent;
    private final List<Constraint> constraints;
    private final SolverAgent solverAgent;

    // Our information
    private float threshold;
    private Integer currentValue;
    private HashMap<String, Integer> currentContext;
    private Boolean receivedTerminate;
    private Boolean terminated;

    private Float minLowerBounds;   // optimiseLowerBoundsCost()
    private Integer minLowerBoundsValue;
    private Float minUpperBounds;    // optimiseUpperBoundsCost()
    private Integer minUpperBoundsValue;

    // Dirty flags
    private boolean updatesSent;
    private boolean costSent;
    private boolean thresholdSent;

    // Mappings of our possible values to child node information
    private HashMap<Integer, HashMap<String, Float>> childLowerBounds;
    private HashMap<Integer, HashMap<String, Float>> childUpperBounds;
    private HashMap<Integer, HashMap<String, Float>> childThresholds;
    private HashMap<Integer, HashMap<String, HashMap<String, Integer>>> childContexts;

    public AdoptSolver (Variable assignedVariable, DFSTree dfsTree, List<Constraint> constraints, SolverAgent solverAgent) {
        this.assignedVariable = assignedVariable;
        this.constraints = constraints;
        this.solverAgent = solverAgent;

        this.allChildren = dfsTree.GetAllChildren(assignedVariable).stream().map(Variable::getName).collect(Collectors.toList());
        this.directChildren = dfsTree.GetChildren(assignedVariable, false).stream().map(Variable::getName).collect(Collectors.toList());

        List<String> parents = dfsTree.GetParents(assignedVariable, false).stream().map(Variable::getName).collect(Collectors.toList());
        this.parent = (parents.isEmpty()) ? null : parents.get(0);

        setup();
    }

    public void setup () {
        threshold = 0;
        currentContext = new HashMap<>();
        receivedTerminate = false;
        terminated = false;

        childLowerBounds = new HashMap<>();
        childUpperBounds = new HashMap<>();
        childThresholds = new HashMap<>();
        childContexts = new HashMap<>();

        for (Integer d : assignedVariable.getDomain().getValues()) {
            childLowerBounds.put(d, new HashMap<>());
            childUpperBounds.put(d, new HashMap<>());
            childThresholds.put(d, new HashMap<>());
            childContexts.put(d, new HashMap<>());

            for (String child : directChildren) {
                childLowerBounds.get(d).put(child, 0f);
                childUpperBounds.get(d).put(child, Float.POSITIVE_INFINITY);
                childThresholds.get(d).put(child, 0f);
                childContexts.get(d).put(child, new HashMap<>());
            }
        }

        updatesSent = false;
        costSent = false;
        thresholdSent = false;

        // Initialise variables -- values will be overwritten in maintainThresholdInvariant()
        minLowerBounds = 0f;
        minUpperBounds = 0f;
        minLowerBoundsValue = 0;
        minUpperBoundsValue = 0;

        // Get the value that minimises LB
        maintainThresholdInvariant();
        currentValue = minLowerBoundsValue;
    }

    public void start () {
        System.out.println("Starting ADOPT for " + assignedVariable.getName());

        // Root only
        if (parent == null) {
            receivedTerminate = true;
        }

        backtrack();
    }

    public InfoMessage getInfo () {
        return new InfoMessage(assignedVariable.name, currentValue, currentCost(), terminated,
                minLowerBounds, minLowerBoundsValue, minUpperBounds, minUpperBoundsValue, threshold);
    }

    // Handle early threshold change from parent
    public void onThreshold (float t, HashMap<String, Integer> context) {
        if (compatibleContexts(context, currentContext)) {
            threshold = t;
            maintainThresholdInvariant();
            backtrack();
        }
    }

    // Handle terminate message from parent
    public void onTerminate (HashMap<String, Integer> context) {
        receivedTerminate = true;
        currentContext = context;
        costSent = false;

        maintainThresholdInvariant();
        backtrack();
    }

    // Handle value message from (psuedo)parent
    public void onValue (String other, Integer otherValue) {
        if (!receivedTerminate) {
            if (!otherValue.equals(currentContext.getOrDefault(other, null))) {
                // Add the variable assignment to our context
                currentContext.put(other, otherValue);
                costSent = false;
            }

            // Reset any incompatible children
            for (Integer d : assignedVariable.getDomain().getValues()) {
                for (String child : directChildren) {
                    if (!compatibleContexts(childContexts.get(d).get(child), currentContext)) {
                        // Reset child information
                        childLowerBounds.get(d).put(child, 0f);
                        childUpperBounds.get(d).put(child, Float.POSITIVE_INFINITY);
                        childThresholds.get(d).put(child, 0f);
                        childContexts.get(d).put(child, new HashMap<>());
                    }
                }
            }

            maintainThresholdInvariant();
            backtrack();
        }
    }

    // Handle cost message from child
    public void onCost (String other, HashMap<String, Integer> otherContext, float otherLB, float otherUB) {
        Integer valueInOtherContext = otherContext.get(assignedVariable.getName());

        otherContext.remove(assignedVariable.getName());

        //
        if (!receivedTerminate) {
            for (String x : otherContext.keySet()) {
                // Check if not a neighbour
                // TODO: Do pseudoparents count as neighbours?
                if (!parent.equals(x) && !allChildren.contains(x)) {
                    currentContext.put(x, otherContext.get(x));
                }
            }

            for (Integer d : assignedVariable.getDomain().getValues()) {
                for (String child : directChildren) {
                    if (!compatibleContexts(childContexts.get(d).get(child), currentContext)) {
                        // Reset child information
                        childLowerBounds.get(d).put(child, 0f);
                        childUpperBounds.get(d).put(child, Float.POSITIVE_INFINITY);
                        childThresholds.get(d).put(child, 0f);
                        childContexts.get(d).put(child, new HashMap<>());

                        thresholdSent = false;
                    }
                }
            }
        }

        // Update local child information
        if (compatibleContexts(otherContext, currentContext)) {
            //System.out.println(otherContext);
            //System.out.println(valueInOtherContext + " - " + other + " - " + otherLB);
            //System.out.println(childLowerBounds);
            if (valueInOtherContext != null) {
                childLowerBounds.get(valueInOtherContext).put(other, otherLB);
                childUpperBounds.get(valueInOtherContext).put(other, otherUB);
                childContexts.get(valueInOtherContext).put(other, otherContext);

                maintainChildThresholdInvariant();
            }
        }
        maintainThresholdInvariant();
        backtrack();
    }

    public void backtrack () {
        if (threshold == minUpperBounds) {
            currentValue = minUpperBoundsValue;
            updatesSent = false;
        } else if (lowerBounds(currentValue) > threshold) {
            currentValue = minLowerBoundsValue;
            updatesSent = false;
        }

        // Send value messages
        if (!updatesSent) {
            Data valueMsg = new Data("Adopt.value", new ValueMessage(assignedVariable.getName(), currentValue), null);
            for (String child : allChildren) {
                solverAgent.sendMessage(valueMsg, child);
            }
        }

        if (maintainAllocationInvariant()) {
            if (threshold == minUpperBounds) {
                // If we have terminated or am root
                if (receivedTerminate || parent == null) {
                    if (!updatesSent) {
                        // Send terminate message
                        HashMap<String, Integer> context = new HashMap<>(currentContext);
                        context.put(assignedVariable.getName(), currentValue);
                        Data terminateMsg = new Data("Adopt.terminate", new TerminateMessage(context), null);

                        for (String child : directChildren) {
                            //System.out.println("Sending terminate to " + child);
                            solverAgent.sendMessage(terminateMsg, child);
                        }
                    }

                    // TODO: Stop execution

                    if (!terminated) {
                        System.out.println(assignedVariable.getName() + " terminated -> currentValue: " + currentValue + ", costing: " + currentCost() + "\n" +
                                "\tThreshold: " + threshold + ", thresholdCost: " + thresholdCost() + "\n" +
                                "\tLB(" + minLowerBoundsValue + " -> " + minLowerBounds + "), UB(" + minUpperBoundsValue + " -> " + minUpperBounds + ")\n" +
                                "\tlb -> " + childLowerBounds + "\n" +
                                "\tt -> " + childThresholds + "\n" +
                                "\tub -> " + childUpperBounds + "\n");
                    }

                    terminated = true;
                    return;
                }
            }
        }

        // Send cost message
        if (!costSent) {
            Data costMsg = new Data("Adopt.cost", new CostMessage(assignedVariable.getName(), currentContext,
                    minLowerBounds, minUpperBounds), null);
            solverAgent.sendMessage(costMsg, parent);
            costSent = true;
        }

        updatesSent = true;
    }

    public void maintainThresholdInvariant () {
        optimiseLowerBounds();
        optimiseUpperBounds();

        // Maintain that: lb <= threshold <= ub
        if (threshold < minLowerBounds) {
            threshold = minLowerBounds;
            thresholdSent = false;
        }

        if (threshold > minUpperBounds) {
            threshold = minUpperBounds;
            thresholdSent = false;
        }
    }

    // TODO: All our problems lie here -- threshold doesn't equal thresholdCost()
    public boolean maintainAllocationInvariant () {
        maintainThresholdInvariant();
        boolean maintained = true;

        // Maintain that: threshold = thresholdCost()
        while (threshold > thresholdCost()) {
            Optional<String> c = directChildren.stream().filter(child -> {
                return childUpperBounds.get(currentValue).get(child) > childThresholds.get(currentValue).get(child);
            }).findFirst();

            if (c.isPresent()) {
                // Increment threshold
                childThresholds.get(currentValue).put(c.get(), childThresholds.get(currentValue).get(c.get()) + 1);
                thresholdSent = false;
            } else {
                // Probably will break invariant
                System.out.println(assignedVariable.getName() + " alloc (>) -> " +  threshold + " should equal " + thresholdCost() + "\n" +
                        "\tLB(" + minLowerBoundsValue + " -> " + minLowerBounds + "), UB(" + minUpperBoundsValue + " -> " + minUpperBounds + ")\n" +
                        "\tlb -> " + childLowerBounds.get(currentValue) + "\n" +
                        "\tt -> " + childThresholds.get(currentValue) + "\n" +
                        "\tub -> " + childUpperBounds.get(currentValue) + "\n");
                break;
            }
        }

        while (threshold < thresholdCost()) {
            Optional<String> c = directChildren.stream().filter(child -> {
                return childThresholds.get(currentValue).get(child) > childLowerBounds.get(currentValue).get(child);
            }).findFirst();

            if (c.isPresent()) {
                // Decrement threshold
                childThresholds.get(currentValue).put(c.get(), childThresholds.get(currentValue).get(c.get()) - 1);
                thresholdSent = false;
            } else {
                // Probably will break invariant
                System.out.println(assignedVariable.getName() + " alloc (<) -> " +  threshold + " should equal " + thresholdCost() + "\n" +
                        "\tLB(" + minLowerBoundsValue + " -> " + minLowerBounds + "), UB(" + minUpperBoundsValue + " -> " + minUpperBounds + ")\n" +
                        "\tlb -> " + childLowerBounds.get(currentValue) + "\n" +
                        "\tt -> " + childThresholds.get(currentValue) + "\n" +
                        "\tub -> " + childUpperBounds.get(currentValue) + "\n");
                maintained = false;
                break;
            }
        }

        // Send threshold message
        if (!thresholdSent) {
            for (String child : directChildren) {
                Data thresholdMsg = new Data("Adopt.threshold", new ThresholdMessage(
                        childThresholds.get(currentValue).get(child), currentContext), null);
                solverAgent.sendMessage(thresholdMsg, child);
            }
            thresholdSent = true;
        }

        return maintained;
    }

    public void maintainChildThresholdInvariant () {
        // Maintain that: lb <= t <= ub for all child information
        for (Integer d : assignedVariable.getDomain().getValues()) {
            for (String child : directChildren) {
                // Enforce lb <= t
                while (childLowerBounds.get(d).get(child) > childThresholds.get(d).get(child)) {
                    // Increment child threshold
                    childThresholds.get(d).put(child, childThresholds.get(d).get(child) + 1);
                    thresholdSent = false;
                }

                // Enforce t <= ub
                while (childThresholds.get(d).get(child) > childUpperBounds.get(d).get(child)) {
                    // Decrement child threshold
                    childThresholds.get(d).put(child, childThresholds.get(d).get(child) - 1);
                    thresholdSent = false;
                }
            }
        }
    }

    public float currentCost () {
        return getCost(currentValue);
    }

    public float getCost (Integer d) {
        // TODO: Cost of assigning the currentValue. Q: Do we include the constraints on children, or just parents?
        //       I think we only do parent, pseudoparents, pseudochildren?, as direct children are part of the lower subtree
        //       causing them to add those constraints to lower/upper/threshold

        float totalCost = assignedVariable.evaluate(d);

        HashMap<String, Integer> context = new HashMap<>(currentContext);
        context.put(assignedVariable.getName(), d);

        for (Constraint c : constraints) {
            // For only constraints covered by our currentContext
            List<String> variableNames = new ArrayList<>(c.variableNames());
            if (context.keySet().containsAll(variableNames)) {
                totalCost += c.evaluate(context);
            }
        }

        return totalCost;
    }

    public float thresholdCost () {
        float total = currentCost();
        for (String child : directChildren) {
            total += childThresholds.get(currentValue).get(child);
        }
        return total;
    }

    public float lowerBounds (Integer d) {
        float total = getCost(d);
        for (String child : directChildren) {
            total += childLowerBounds.get(d).get(child);
        }
        return total;
    }

    public void optimiseLowerBounds () {
        float prevLB = minLowerBounds;
        float min = Float.POSITIVE_INFINITY;
        for (Integer d : assignedVariable.getDomain().getValues()) {
            float lb = lowerBounds(d);
            if (lb < min) {
                min = lb;
                minLowerBoundsValue = d;
            }
        }
        minLowerBounds = min;
        if (min != prevLB) {
            costSent = false;
        }
    }

    public float upperBounds (Integer d) {
        float total = getCost(d);
        for (String child : directChildren) {
            total += childUpperBounds.get(d).get(child);
        }
        return total;
    }

    public void optimiseUpperBounds () {
        float prevUB = minLowerBounds;
        float min = Float.POSITIVE_INFINITY;
        for (Integer d : assignedVariable.getDomain().getValues()) {
            float ub = upperBounds(d);
            if (ub < min) {
                min = ub;
                minUpperBoundsValue = d;
            }
        }
        minUpperBounds = min;
        if (min != prevUB) {
            costSent = false;
        }
    }

    public Boolean compatibleContexts (HashMap<String, Integer> c1, HashMap<String, Integer> c2) {
        boolean compatible = true;
        // Check that no key assignments conflict
        for (String v : c1.keySet()) {
            if (c2.containsKey(v) && !c1.get(v).equals(c2.get(v))) {
                compatible = false;
                break;
            }
        }
        return compatible;
    }

    public void handleMessage (Data content, String[] typeTree) {
        // If a message remains to be processed
        // TODO: Does line 51 (Terminate execution) mean "Do not accept any new messages"?
        //if (content != null && !terminated) {
        if (content != null) {
            if (typeTree.length == 2 && typeTree[0].equals("Adopt")) {
                switch (typeTree[1]) {
                    case "threshold":
                        ThresholdMessage thresholdMsg = (ThresholdMessage)content.getValue();
                        onThreshold(thresholdMsg.getThresh(), thresholdMsg.getContext());
                        break;
                    case "terminate":
                        TerminateMessage terminateMsg = (TerminateMessage)content.getValue();
                        onTerminate(terminateMsg.getContext());
                        break;
                    case "cost":
                        CostMessage costMsg = (CostMessage)content.getValue();
                        onCost(costMsg.getOther(), costMsg.getOtherContext(), costMsg.getOtherLB(), costMsg.getOtherUB());
                        break;
                    case "value":
                        ValueMessage valueMsg = (ValueMessage)content.getValue();
                        onValue(valueMsg.getOther(), valueMsg.getOtherValue());
                        break;
                }
            }
        }
    }
}
