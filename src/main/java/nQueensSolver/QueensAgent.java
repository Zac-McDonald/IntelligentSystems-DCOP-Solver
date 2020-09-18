package nQueensSolver;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An agent that provides the IQueens solver
 */
@Agent
@ProvidedServices(@ProvidedService(type= IQueens.class))
public class QueensAgent implements IQueens{
    /**
     * @param n The number of queens to solve for
     * @return A list of the queen positions or an empty list if no solution found.
     */
    @Override
    public IFuture<List<Integer>> solveNQueens(int n) {
        Model model = new Model(n + "-queens problem");
        IntVar[] vars = new IntVar[n];
        for(int q = 0; q < n; q++){
            vars[q] = model.intVar("Q_"+q, 1, n);
        }
        for(int i  = 0; i < n-1; i++){
            for(int j = i + 1; j < n; j++){
                model.arithm(vars[i], "!=",vars[j]).post();
                model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
                model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
            }
        }
        Solution solution = model.getSolver().findSolution();
        if(solution != null){
//            List<Integer> ret = new ArrayList<>();
//            for (IntVar var : vars) {
//                ret.add(var.getValue());
//            }
//            return new Future<>(ret);
            // Below does same thing as above code but cleaner and more 'modern'
            return new Future<>(Arrays.stream(vars)
                    .map(IntVar::getValue)
                    .collect(Collectors.toList()));
        }
        return new Future<>(new ArrayList<>()); // Empty list
    }
}
