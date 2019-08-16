package checkers;

import others.MonteCarloAlgorithm;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MonteCarloImplementation extends MonteCarloAlgorithm<ChineseCheckersState, MoveCommand, GameParticipant> {

    private ChineseCheckersModel model;
    private String player;

    public MonteCarloImplementation(double secondsPerTurn, int maxMovesPerPlay, ChineseCheckersModel model, String player) {
        super(secondsPerTurn, maxMovesPerPlay);
        this.player = player;
        this.model = model;
    }

    /**
     * Given the game in one state, produce the list of legal moves from
     * that state.  If from is a state where a winner has not been
     * determined, the returned list must have at least one element.
     */

    //Determined by who played last!
    @Override
    public List<MoveCommand> getLegalMoves(ChineseCheckersState from){
        ArrayList<MoveCommand> result = new ArrayList<>();
        HashMap<String, HashMap<Point, Point>> positionsByPlayer = from.getPositionsByPlayer();

        for(Point piece : positionsByPlayer.get(from.getCurrentPlayer()).keySet()){
            tryPiece(piece, result, from);
        }
        return result;
    }

    /**
     * Apply a move to a game, resulting in a new object representing
     * the state of the game with that move taken.  N.B.:  As specified
     * above, GameStateType is an immutable record of the state of play
     * of the game, at an instant in time.
     */
    @Override
    public ChineseCheckersState applyMove(ChineseCheckersState from, MoveCommand move){

        HashMap<String, HashMap<Point, Point>> positionsByPlayer = from.getPositionsByPlayer();
        HashMap<String, HashMap<Point, Point>> positionsByPlayerCopy = new HashMap<>();

        for(String player : positionsByPlayer.keySet()){
            HashMap<Point, Point> pointsCopy = new HashMap<>();
            if(player.equals(from.getCurrentPlayer())){
                //Add the new Point
                Point newPoint = new Point(move.getTo().x, move.getTo().y);
                pointsCopy.put(newPoint, newPoint);
            }
            for(Point p : positionsByPlayer.get(player).keySet()){
                if(p.x != move.getFrom().x || p.y != move.getFrom().y) {
                    Point copy = new Point(p.x, p.y);
                    pointsCopy.put(copy, copy);
                }
            }
            positionsByPlayerCopy.put(player, pointsCopy);
        }
        return new ChineseCheckersState(from.getBoard(), positionsByPlayerCopy, from.getHighlightedPoints(), from.getNextPlayer(), from.getStartingPositions());

    }

    /**
     * Give the team that has won in the given game state, if a winner
     * has been determined. Return null if there is no winning player yet.
     */
    @Override
    public GameParticipant getWinningTeam(ChineseCheckersState state){
        String winner = state.getGameWinner();
        if(winner == null){
            return null;
        }
        return model.getPlayerByColor(winner);
    }

    /**
     *  Give the last team to have moved for a given game state.
     */
    @Override
    public GameParticipant getLastTeamToMove(ChineseCheckersState state){
        return model.getPlayerByColor(state.getLastPlayer());
    }

    /**
     *  Apply a heuristic to select the best move out of the given list.
     *  This method picks one at random.  Subclasses should override this
     *  method to provide a better heuristic.
     */
    @Override
    public MoveRecord<MoveCommand, ChineseCheckersState>
    heuristicChoice(List<MoveRecord<MoveCommand, ChineseCheckersState>> moves)
    {
        int size = moves.size();
        assert size > 0;
        if (size == 1) {
            return moves.get(0);
        }

        int bestPoints = 1;
        MoveRecord<MoveCommand, ChineseCheckersState> bestMove = moves.get(0);

        Heuristic h = Heuristic.getInstance();
        HashMap<RankedPosition, Integer> heuristicMap = Heuristic.getMovesByColor(bestMove.state.getLastPlayer());
        for(MoveRecord<MoveCommand, ChineseCheckersState> possibleMove : moves){
            Point from = possibleMove.move.getFrom();
            Point to = possibleMove.move.getTo();
            int pointsEarned = heuristicMap.get(new RankedPosition(from.x, from.y, 0)) - heuristicMap.get(new RankedPosition(to.x, to.y, 0));
            if(pointsEarned > bestPoints){
                bestPoints = pointsEarned;
                bestMove = possibleMove;
            } else if(pointsEarned == bestPoints){
                Random rand = new Random();
                int chance = rand.nextInt();
                if(chance % 2 == 0){
                    bestMove = possibleMove;
                }
            }
        }
        return bestMove;
    }

    @Override
    protected boolean cancelGetPlay() {
        if( !(model.getCurrentPlayer().getColor().equals(player)) ){
            return true;
        }
        return false;
    }


    /**
     * Some Utility Functions
     *
     */

    private void tryPiece(Point piece, ArrayList<MoveCommand> moves, ChineseCheckersState state){
        ArrayList<Point> possiblePoints = new ArrayList<>();
        getNeighboringMoves(piece, possiblePoints, moves, state);
    }

    /**
     * from a given game piece point find all board locations that piece can move to
     */
    private void getNeighboringMoves(Point current, ArrayList<Point> possiblePoints, ArrayList<MoveCommand> moves, ChineseCheckersState state) {
        //if point is occupied, add to searchAround
        //else add to possiblePoints
        //you can't move then hop

        Point[] neighbors = new Point[]{new Point(current.x + 1, current.y + 1),
                new Point(current.x + 1, current.y - 1),
                new Point(current.x - 1, current.y + 1),
                new Point(current.x - 1, current.y - 1),
                new Point(current.x - 2, current.y),
                new Point(current.x + 2, current.y)};

        for (Point neighbor : neighbors) {
            if (BoardUtil.onBoard(neighbor)) {
                if (!("".equals(findOccupant(neighbor, state)))) {
                    getHoppingMoves(current, current, neighbor, possiblePoints, moves, state, new ArrayList<>());
                } else {
                    if(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), current) ||
                            !(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), current)) &&
                                    !(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), neighbor))) {
                        if(BoardUtil.inDestination(model.getPlayerByColor(state.getCurrentPlayer()), neighbor, model) ||
                                !BoardUtil.inDestination(model.getPlayerByColor(state.getCurrentPlayer()), current, model)) {
                            possiblePoints.add(neighbor);
                        }
                        moves.add(new MoveCommand(null, neighbor, current));
                    }
                }
            }
        }
    }

    /**
     *
     * @param original The very first point we started at. Useful for point calculations
     * @param current Where we are after/before a jump
     * @param other The other player we can jump over
     * @param possiblePoints
     * @param moves
     * @param state The Game State we are "playing" currently
     */
    private void getHoppingMoves(Point original, Point current, Point other, ArrayList<Point> possiblePoints,
                                 ArrayList<MoveCommand> moves, ChineseCheckersState state, ArrayList<Point> discard){
        Point attempt = new Point(current.x + 2*(other.x - current.x), current.y + 2*(other.y - current.y) );
        //Make sure it's on the board!
        if(!(BoardUtil.onBoard(attempt))){
            return;
        }
        if ("".equals(findOccupant(attempt, state))){
            //Make sure we haven't found this point already!
            for(Point p : possiblePoints){
                if(attempt.x == p.x && attempt.y == p.y){
                    return;
                }
            }
            for(Point p : discard){
                if(attempt.x == p.x && attempt.y == p.y){
                    return;
                }
            }

            //Filter whether you are allowed to enter home or not
            if(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), current) ||
                    !(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), current))
                            && !(BoardUtil.inHome(model.getPlayerByColor(state.getCurrentPlayer()), attempt))) {
                if(BoardUtil.inDestination(model.getPlayerByColor(state.getCurrentPlayer()), attempt, model) ||
                        !BoardUtil.inDestination(model.getPlayerByColor(state.getCurrentPlayer()), original, model)) {
                    possiblePoints.add(attempt);
                    moves.add(new MoveCommand(null, attempt, original));
                }
                else{
                    discard.add(attempt);
                }

            }
            Point[] neighbors = new Point[]{new Point(attempt.x + 1, attempt.y + 1),
                    new Point(attempt.x + 1, attempt.y - 1),
                    new Point(attempt.x - 1, attempt.y + 1),
                    new Point(attempt.x - 1, attempt.y - 1),
                    new Point(attempt.x - 2, attempt.y),
                    new Point(attempt.x + 2, attempt.y)};

            //our hop leads us next to another point, try to hop that one too!
            for (Point neighbor : neighbors) {
                if (BoardUtil.onBoard(neighbor)) {
                    if (!("".equals(findOccupant(neighbor, state)))) {
                        getHoppingMoves(original, attempt, neighbor, possiblePoints, moves, state, discard);
                    }
                }
            }
        }
    }

    /**
     * returns the color of the player on a spot, or empty string if its not occupied
     */
    public String findOccupant(Point p, ChineseCheckersState state){
        for(String opponent : state.getPositionsByPlayer().keySet()){
            if(state.getPositionsByPlayer().get(opponent).get(p) != null){
                return opponent;
            }
        }
        return "";
    }


}
