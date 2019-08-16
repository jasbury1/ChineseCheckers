package others;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * This is a straightforward implementation of the Monte Carlo Algorithm,
 * https://en.wikipedia.org/wiki/Monte_Carlo_algorithm .  This is an algorithm
 * for playing a game that doesn't require extensive knowledge of game 
 * strategies or rules.  It works by making random moves to "play" a game, 
 * and then further exploring statistically promising paths down the move tree.
 * <p>
 * In order to use this algorithm, it is necessary to define a heuristic
 * function that provides some idea of what a "good" board arrangement is.
 * See the abstract method heuristicChoice, below.
 * <p>
 * GameStateType must be an immutable record of the state of play of the game,
 * at an instant in time.  It must be usable as a hash table key.
 * <p>
 * MoveType must be a type that represents a move, that can transition from one
 * GameStateType to another.
 * <p>
 * TeamType must identify a team.  It must be possible to compare two teams
 * using equals().
 * <p>
 * Note that method cancelGetPlay() may be overridden to cancel a call
 * to getPlay() i nthe middle.  You might want to do this, for example, if the 
 * user hits "undo" while the algorithm is calculating a move.  This is not
 * necessary, however:  it's fine to just let the algorithm run to completion 
 * in this case, and then throw away the (now obsolete) result.  As a 
 * pragmatic matter, one second of calculation time has been found to be 
 * long enough to generate good results.  If undo is pressed there's at 
 * most one second's worth of extra delay.
 */
public abstract class MonteCarloAlgorithm<GameStateType, MoveType, TeamType> {

    private final long nsPerTurn;
    private final int maxMovesPerPlay;
    private static float C = 1.4f;
    // This is a constant that's part of the algorithm.  1.4 is a 
    // value that's frequently used.

    protected final Random random = new Random();

    /**
     * Create an  object to calculate a move in a game.  One second/play
     * is reasonable, and for Chinese checkers, 500 is a reasonable value
     * for max moves/play.
     */
    protected MonteCarloAlgorithm(double secondsPerTurn, int maxMovesPerPlay) {
        this.nsPerTurn = Math.round(secondsPerTurn * 1000 * 1_000_000);
        this.maxMovesPerPlay = maxMovesPerPlay;     
    }

    //
    // A little class to stash away the # of wins and plays associated 
    // with a GameState
    //
    private static final class Visit<GameStateType> {
        final GameStateType state;
        int wins = 0;
        int plays = 0;

        Visit(GameStateType state) {
            this.state = state;
        }

        @Override public String toString() {
            return "[wins " + wins + " plays " + plays;
        }
    }

    //
    // A little class to hold a move, the resulting state, and the score.
    //
    protected static final class MoveRecord<MoveType, GameStateType> {

        public final MoveType move;
        public final GameStateType state;  // New state, after the move

        private final Visit<GameStateType> visit;   // May be null
        private final float score;	   // May be Float.NaN

        MoveRecord(MoveType move, GameStateType state, 
                   Visit<GameStateType> visit, float score) {
            this.move = move;
            this.state = state;
            this.visit = visit;
            this.score = score;
        }
    }

    //
    // Get the move we're going to try, starting at the given state
    //

    /**
     * Get a recommended move, starting from the state from.
     *
     * @param from  The starting game state.  It must not be a state
     *              where one of the players has won.
     *
     * @return  The best move for the player whose turn it is.  If
     *          cancelGetPlay() is overriden to return true, that may
     *          cause a null return value.
     */
    public MoveType getPlay(GameStateType from) {
        assert getWinningTeam(from) == null;
        List<MoveType> moves = getLegalMoves(from);
        assert !moves.isEmpty();
        Map<GameStateType, Visit<GameStateType>> visits = new HashMap<>();
        long deadline = System.nanoTime() + nsPerTurn;
        while (System.nanoTime() < deadline) {
            if (cancelGetPlay()) {
                return null;
            }
            runSimulation(from, visits);
        }

        // Pick the move(s) with the highest percentage of wins
        float bestScore = Float.NEGATIVE_INFINITY;
        List<MoveRecord<MoveType, GameStateType>> bestMoves = null;
        for (MoveType move : moves) {
            GameStateType newState = applyMove(from, move);
            Visit<GameStateType> visit = visits.get(newState);  // can be null
            float score = getScore(visit);
            if (score < bestScore) {
                continue;
            }
            if (score > bestScore) {
                bestMoves = new ArrayList<>();
                bestScore = score;
            }
            bestMoves.add(new MoveRecord<MoveType, GameStateType>(
                                move, newState, visit, score));
        }
        assert bestMoves != null && !bestMoves.isEmpty();
        return heuristicChoice(bestMoves).move;
    }

    private float getScore(Visit<GameStateType> visit) {
        if (visit != null && visit.plays > 0) {
            return (float) visit.wins / (float) visit.plays;
        } else {
            return 0f;
        }
    }

    private void runSimulation(
            GameStateType from, 
            Map<GameStateType, Visit<GameStateType>>  visits) 
    {
        boolean expand = true;
        List<GameStateType> visitedStates = new ArrayList<GameStateType>();
        TeamType winner = null;
        GameStateType currState = from;
        for (int i = 0; i <= maxMovesPerPlay; i++) {
            List<MoveRecord<MoveType, GameStateType>> bestMoves 
                    = new ArrayList<>();
            boolean knowAllScores = true;
            List<MoveType> moves = getLegalMoves(currState);
            assert !moves.isEmpty();
            for (MoveType move : moves) {
                GameStateType nextState = applyMove(currState, move);
                Visit<GameStateType> visit = null;
                if (knowAllScores) {
                    visit = visits.get(nextState);  // can be null
                    if (visit == null) {
                        knowAllScores = false;
                    }
                }
                bestMoves.add(new MoveRecord<MoveType, GameStateType>
                                        (move, nextState, visit, Float.NaN));  
                                        // Score is unknown
            }
            // If we have scores for all of these moves, we prune the list
            // down to only the best scores.
            if (knowAllScores) {
                float logTotal = 0f;
                List<MoveRecord<MoveType, GameStateType>> newBestMoves = null;
                for (MoveRecord<MoveType, GameStateType> move : bestMoves) {
                    logTotal += move.visit.plays;
                }
                logTotal = (float) Math.log(logTotal);
                float bestScore = Float.NEGATIVE_INFINITY;
                for (MoveRecord<MoveType, GameStateType> move : bestMoves) {
                    Visit<GameStateType> visit = move.visit;
                    assert visit.plays > 0;
                    float scoreForMove = visit.wins / (float) visit.plays
                            + (float) (C * Math.sqrt(logTotal / visit.plays));
                    if (scoreForMove < bestScore) {
                        continue;
                    }
                    if (scoreForMove > bestScore) {
                        bestScore = scoreForMove;
                        newBestMoves = new ArrayList<>();
                    }
                    newBestMoves.add(new MoveRecord<MoveType, GameStateType>(
                            move.move, move.state, visit, scoreForMove));
                }
                bestMoves = newBestMoves;
            }
            MoveRecord<MoveType, GameStateType> chosen 
                    = heuristicChoice(bestMoves);
            if (expand && chosen.visit != null) {
                expand = false;
                visits.put(chosen.state, 
                           new Visit<GameStateType>(chosen.state));
            }
            visitedStates.add(chosen.state);
            winner = getWinningTeam(chosen.state);
            if (winner != null) {
                break;
            }
            currState = chosen.state;
        }

        for (GameStateType newlyVisitedState : visitedStates) {
            Visit<GameStateType> visit = visits.get(newlyVisitedState);
            if (visit != null) {
                visit.plays++;
                if (getLastTeamToMove(newlyVisitedState).equals(winner)) {
                    visit.wins++;
                }
            }
        }
    }

    /**
     * Given the game in one state, produce the list of legal moves from
     * that state.  If from is a state where a winner has not been
     * determined, the returned list must have at least one element.
     */
    protected abstract List<MoveType> getLegalMoves(GameStateType from);

    /**
     * Apply a move to a game, resulting in a new object representing
     * the state of the game with that move taken.  N.B.:  As specified
     * above, GameStateType is an immutable record of the state of play 
     * of the game, at an instant in time.
     */
    protected abstract GameStateType applyMove(GameStateType from, 
                                               MoveType move);

    /**
     * Give the team that has won in the given game state, if a winner 
     * has been determined. Return null if there is no winning player yet.
     */
    protected abstract TeamType getWinningTeam(GameStateType state);

    /**
     *  Give the last team to have moved for a given game state.
     */
    protected abstract TeamType getLastTeamToMove(GameStateType state);

    /**
     *  Apply a heuristic to select the best move out of the given list.
     *  This method picks one at random.  Subclasses should override this
     *  method to provide a better heuristic.
     */
    protected MoveRecord<MoveType, GameStateType>
    heuristicChoice(List<MoveRecord<MoveType, GameStateType>> moves)
    {
        int size = moves.size();
        assert size > 0;
        if (size == 1) {
            return moves.get(0);
        }
        int i = random.nextInt(size);
        return moves.get(i);
    }

    /**
     * Return true if a call to getPlay(), currently in progress, should
     * be canceled.  This will be called on the thread that is calling
     * getPlay().  Overriding this method is optional.  If this method is
     * overridden, presumably some kind of locking will be required to 
     * determine if cancelling is appropriate.
     */
    protected boolean cancelGetPlay() {
        return false;
    }
}
