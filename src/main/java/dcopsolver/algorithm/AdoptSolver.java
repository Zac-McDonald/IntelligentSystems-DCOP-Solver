package dcopsolver.algorithm;

import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.Variable;
import message.Data;
import message.SolverAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AdoptSolver {
    private final Variable assignedVariable;
    private final List<Variable> allChildren;
    private final List<Variable> directChildren;
    private final Variable parent;
    private final List<Constraint> constraints;
    private final SolverAgent solverAgent;

    // Our information
    private float threshold;
    private Integer currentValue;
    private HashMap<Variable, Integer> currentContext;
    private Boolean receivedTerminate;

    // Mappings of our possible values to child node information
    private HashMap<Integer, HashMap<Variable, Float>> childLowerBounds;
    private HashMap<Integer, HashMap<Variable, Float>> childUpperBounds;
    private HashMap<Integer, HashMap<Variable, Float>> childThresholds;
    private HashMap<Integer, HashMap<Variable, HashMap<Variable, Integer>>> childContexts;

    public AdoptSolver (Variable assignedVariable, DFSTree dfsTree, List<Constraint> constraints, SolverAgent solverAgent) {
        this.assignedVariable = assignedVariable;
        this.constraints = constraints;
        this.solverAgent = solverAgent;

        this.allChildren = dfsTree.GetAllChildren(assignedVariable);
        this.directChildren = dfsTree.GetChildren(assignedVariable, false);

        List<Variable> parents = dfsTree.GetParents(assignedVariable, false);
        this.parent = (parents.isEmpty()) ? null : parents.get(0);
    }

    public void start () {
        threshold = 0;
        currentContext = new HashMap<>();
        receivedTerminate = false;

        for (Integer d : assignedVariable.getDomain().getValues()) {
            for (Variable child : directChildren) {
                childLowerBounds.put(d, new HashMap<>());
                childLowerBounds.get(d).put(child, 0f);

                childUpperBounds.put(d, new HashMap<>());
                childUpperBounds.get(d).put(child, Float.POSITIVE_INFINITY);

                childThresholds.put(d, new HashMap<>());
                childThresholds.get(d).put(child, 0f);

                childContexts.put(d, new HashMap<>());
                childContexts.get(d).put(child, new HashMap<>());
            }
        }

        // Get the value that minimises LB
        AtomicInteger minLBValue = new AtomicInteger();
        optimiseLowerBounds(minLBValue);
        currentValue = minLBValue.get();

        backtrack();
    }

    // Handle early threshold change from parent
    public void onThreshold (float t, HashMap<Variable, Integer> context) {
        if (compatibleContexts(context, currentContext)) {
            threshold = t;
            maintainThresholdInvariant();
            backtrack();
        }
    }

    // Handle terminate message from parent
    public void onTerminate (HashMap<Variable, Integer> context) {
        receivedTerminate = true;
        currentContext = context;
        backtrack();
    }

    // Handle value message from (psuedo)parent
    public void onValue (Variable other, Integer otherValue) {
        if (!receivedTerminate) {
            // Add the variable assignment to our context
            currentContext.put(other, otherValue);

            // Reset any incompatible children
            for (Integer d : assignedVariable.getDomain().getValues()) {
                for (Variable child : directChildren) {
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
    public void onCost (Variable other, HashMap<Variable, Integer> otherContext, float otherLB, float otherUB) {
        Integer valueInOtherContext = otherContext.get(assignedVariable);
        // TODO: Make sure that on the same platform, otherContext is a copy and not a reference to the original hashmap
        otherContext.remove(assignedVariable);

        //
        if (!receivedTerminate) {
            for (Variable x : otherContext.keySet()) {
                // TODO: Checks if not neighbour -- does this include/exclude pseudos?
                if (parent.equals(x) || allChildren.contains(x)) {
                    currentContext.put(x, otherContext.get(x));
                }
            }

            for (Integer d : assignedVariable.getDomain().getValues()) {
                for (Variable child : directChildren) {
                    if (!compatibleContexts(childContexts.get(d).get(child), currentContext)) {
                        // Reset child information
                        childLowerBounds.get(d).put(child, 0f);
                        childUpperBounds.get(d).put(child, Float.POSITIVE_INFINITY);
                        childThresholds.get(d).put(child, 0f);
                        childContexts.get(d).put(child, new HashMap<>());
                    }
                }
            }
        }

        // Update local child information
        if (compatibleContexts(otherContext, currentContext)) {
            childLowerBounds.get(valueInOtherContext).put(other, otherLB);
            childUpperBounds.get(valueInOtherContext).put(other, otherUB);
            childContexts.get(valueInOtherContext).put(other, otherContext);

            maintainChildThresholdInvariant();
            maintainThresholdInvariant();
        }

        backtrack();
    }

    public void backtrack () {
        float ub = optimiseUpperBoundsCost();
        if (threshold == ub) {
            currentValue = optimiseUpperBoundsValue();
        } else if (lowerBounds(currentValue) > threshold) {
            currentValue = optimiseLowerBoundsValue();
        }

        // Send value messages
        for (Variable child : allChildren) {
            Data valueMsg = new Data("Adopt.value", new ValueMessage(assignedVariable, currentValue), null);
            solverAgent.sendMessage(valueMsg, child);
        }

        maintainAllocationInvariant();

        ub = optimiseUpperBoundsCost();
        if (threshold == ub) {
            // If we have terminated or am root
            if (receivedTerminate || parent == null) {
                // Send terminate message
                for (Variable child : directChildren) {
                    HashMap<Variable, Integer> context = currentContext;
                    context.put(assignedVariable, currentValue);
                    Data terminageMsg = new Data("Adopt.terminate", new TerminateMessage(context), null);
                    solverAgent.sendMessage(terminageMsg, child);
                }

                // TODO: Stop execution
                System.out.println(solverAgent.getId() + " finished solving: " + assignedVariable.getName() + " = " + currentValue);
            }
        }

        // Send cost message
        Data costMsg = new Data("Adopt.cost", new CostMessage(assignedVariable, currentContext,
                optimiseLowerBoundsCost(), optimiseUpperBoundsCost()), null);
        solverAgent.sendMessage(costMsg, parent);
    }

    public void maintainThresholdInvariant () {
        // Maintain that: lb <= threshold <= ub
        float lb = optimiseLowerBoundsCost();
        if (threshold < lb) {
            threshold = lb;
        }

        float ub = optimiseUpperBoundsCost();
        if (threshold > ub) {
            threshold = ub;
        }
    }

    public void maintainAllocationInvariant () {
        // Maintain that: -----
        while (threshold > thresholdCost()) {
            Optional<Variable> c = directChildren.stream().filter(child -> {
                return childUpperBounds.get(currentValue).get(child) > childThresholds.get(currentValue).get(child);
            }).findFirst();

            if (c.isPresent()) {
                // Increment threshold
                childThresholds.get(currentValue).put(c.get(), childThresholds.get(currentValue).get(c.get()) + 1);
            }
        }

        while (threshold < thresholdCost()) {
            Optional<Variable> c = directChildren.stream().filter(child -> {
                return childThresholds.get(currentValue).get(child) > childLowerBounds.get(currentValue).get(child);
            }).findFirst();

            if (c.isPresent()) {
                // Decrement threshold
                childThresholds.get(currentValue).put(c.get(), childThresholds.get(currentValue).get(c.get()) - 1);
            }
        }

        // Send threshold message
        for (Variable child : directChildren) {
            Data thresholdMsg = new Data("Adopt.threshold", new ThresholdMessage(
                    childThresholds.get(currentValue).get(child), currentContext), null);
            solverAgent.sendMessage(thresholdMsg, child);
        }
    }

    public void maintainChildThresholdInvariant () {
        // Maintain that: -----
        for (Integer d : assignedVariable.getDomain().getValues()) {
            for (Variable child : directChildren) {
                while (childLowerBounds.get(d).get(child) > childThresholds.get(d).get(child)) {
                    // Increment child threshold
                    childThresholds.get(d).put(child, childThresholds.get(d).get(child) + 1);
                }

                while (childThresholds.get(d).get(child) > childUpperBounds.get(d).get(child)) {
                    // Decrement child threshold
                    childThresholds.get(d).put(child, childThresholds.get(d).get(child) - 1);
                }
            }
        }
    }

    public float currentCost () {
        // TODO: Cost of assigning the currentValue. Q: Do we include the constraints on children, or just parents?
        //       I think we only do parent, pseudoparents, pseudochildren?, as direct children are part of the lower subtree

        float totalCost = assignedVariable.evaluate(currentValue);

        HashMap<String, Integer> assignment = new HashMap<>();
        currentContext.forEach((v, i) -> assignment.put(v.getName(), i));

        for (Constraint c : constraints) {
            // For only constraints covered by our currentContext
            if (assignment.keySet().containsAll(c.getVariables())) {
                totalCost += c.evaluate(assignment);
            }
        }

        return totalCost;
    }

    public float thresholdCost () {
        float total = currentCost();
        for (Variable child : directChildren) {
            total += childThresholds.get(currentValue).get(child);
        }
        return total;
    }

    public float lowerBounds (Integer d) {
        float total = currentCost();
        for (Variable child : directChildren) {
            total += childLowerBounds.get(d).get(child);
        }
        return total;
    }

    public float optimiseLowerBounds (AtomicInteger bestD) {
        float min = Float.POSITIVE_INFINITY;
        for (Integer d : assignedVariable.getDomain().getValues()) {
            float lb = lowerBounds(d);
            if (lb < min) {
                min = lb;
                bestD.set(d);
            }
        }
        return min;
    }

    public int optimiseLowerBoundsValue () {
        AtomicInteger minValue = new AtomicInteger();
        optimiseLowerBounds(minValue);
        return minValue.get();
    }

    public float optimiseLowerBoundsCost () {
        return optimiseLowerBounds(new AtomicInteger(0));
    }

    public float upperBounds (Integer d) {
        float total = currentCost();
        for (Variable child : directChildren) {
            total += childUpperBounds.get(d).get(child);
        }
        return total;
    }

    public float optimiseUpperBounds (AtomicInteger bestD) {
        float min = Float.POSITIVE_INFINITY;
        for (Integer d : assignedVariable.getDomain().getValues()) {
            float ub = upperBounds(d);
            if (ub < min) {
                min = ub;
                bestD.set(d);
            }
        }
        return min;
    }

    public int optimiseUpperBoundsValue () {
        AtomicInteger minValue = new AtomicInteger();
        optimiseUpperBounds(minValue);
        return minValue.get();
    }

    public float optimiseUpperBoundsCost () {
        return optimiseUpperBounds(new AtomicInteger(0));
    }

    public Boolean compatibleContexts (HashMap<Variable, Integer> c1, HashMap<Variable, Integer> c2) {
        Boolean compatible = true;
        // Check that no key assignments conflict
        for (Variable v : c1.keySet()) {
            if (c2.containsKey(v) && !c1.get(v).equals(c2.get(v))) {
                compatible = false;
                break;
            }
        }
        return compatible;
    }

    public void handleMessage (Data content, String[] typeTree) {
        // If a message remains to be processed
        if (content != null) {
            if (typeTree.length == 2 && typeTree[0] == "Adopt") {
                switch (typeTree[1]) {
                    case "threshold":
                        ThresholdMessage thresholdMsg = (ThresholdMessage)content.getValue();
                        onThreshold(thresholdMsg.t, thresholdMsg.context);
                        break;
                    case "terminate":
                        TerminateMessage terminateMsg = (TerminateMessage)content.getValue();
                        onTerminate(terminateMsg.context);
                        break;
                    case "cost":
                        CostMessage costMsg = (CostMessage)content.getValue();
                        onCost(costMsg.other, costMsg.otherContext, costMsg.otherLB, costMsg.otherUB);
                        break;
                    case "value":
                        ValueMessage valueMsg = (ValueMessage)content.getValue();
                        onValue(valueMsg.other, valueMsg.otherValue);
                        break;
                }
            }
        }
    }

    private class ThresholdMessage {
        public Float t;
        public HashMap<Variable, Integer> context;

        public ThresholdMessage (Float t, HashMap<Variable, Integer> context) {
            this.t = t;
            this.context = context;
        }
    }

    private class TerminateMessage {
        public HashMap<Variable, Integer> context;

        public TerminateMessage (HashMap<Variable, Integer> context) {
            this.context = context;
        }
    }

    private class CostMessage {
        public Variable other;
        public HashMap<Variable, Integer> otherContext;
        public float otherLB;
        public float otherUB;

        public CostMessage (Variable other, HashMap<Variable, Integer> otherContext, float otherLB, float otherUB) {
            this.other = other;
            this.otherContext = otherContext;
            this.otherLB = otherLB;
            this.otherUB = otherUB;
        }
    }

    private class ValueMessage {
        public Variable other;
        public Integer otherValue;

        public ValueMessage (Variable other, Integer otherValue) {
            this.other = other;
            this.otherValue = otherValue;
        }
    }
}
