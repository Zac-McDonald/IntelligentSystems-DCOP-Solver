package nQueensSolver;

import jadex.commons.future.IFuture;

import java.util.List;

/**
 * @author Charles Harold
 */
public interface IQueens {

    IFuture<List<Integer>> solveNQueens (int n);
}
