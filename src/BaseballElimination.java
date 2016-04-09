import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.IntStream;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private final int UNKNOWN = 0;
    private final int IS_ELIMINATED = 1;
    private final int IS_NOT_ELIMINATED = 2;

    private HashMap<String, Integer> names;
    private HashMap<Integer, String> invNames;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] games;
    private int[] isTeamEliminated;
    // private ArrayList<Vector<String>> certificates;
    private Vector<String>[] certificates;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        parseFile(filename);
    }

    // number of teams
    public int numberOfTeams() {
        return wins.length;
    }

    // all teams
    public Iterable<String> teams() {
        return names.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        return wins[ix(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return losses[ix(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remaining[ix(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return games[ix(team1)][ix(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        int x = ix(team);
        if (isTeamEliminated[x] == UNKNOWN) {
            isTeamEliminated[x] = isTrivialEliminated(team) ? IS_ELIMINATED
                    : UNKNOWN;
            if (isTeamEliminated[x] == UNKNOWN) {
                isTeamEliminated[x] = runFordFulkerson(team) ? IS_ELIMINATED
                        : IS_NOT_ELIMINATED;
            }
        }
        return isTeamEliminated[x] == IS_ELIMINATED;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        Iterable<String> iter = null;
        isEliminated(team);
        int x = ix(team);
        if (isTeamEliminated[x] == IS_ELIMINATED) {
            // iter = certificates.get(x);
            iter = certificates[x];
        }
        return iter;
    }

    private int ix(String team) {
        if (!names.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return names.get(team);
    }

    private String xi(int i) {
        if (!invNames.containsKey(i)) {
            throw new java.lang.IllegalArgumentException();
        }
        return invNames.get(i);
    }

    @SuppressWarnings("unchecked")
    private void parseFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            int numTeams = Integer.parseInt(line);
            names = new HashMap<String, Integer>();
            invNames = new HashMap<Integer, String>();
            wins = new int[numTeams];
            losses = new int[numTeams];
            remaining = new int[numTeams];
            games = new int[numTeams][];
            isTeamEliminated = new int[numTeams];
            // certificates = new ArrayList<Vector<String>>(numTeams);
            certificates = (Vector<String>[]) new Vector[numTeams];

            for (int i = 0; i < numTeams; i++) {
                line = br.readLine();
                String name = line.split("\\s+")[0];
                names.put(name, i);
                invNames.put(i, name);
                int[] values = Arrays.stream(line.split("\\s+")).skip(1)
                        .mapToInt(Integer::parseInt).toArray();

                wins[i] = values[0];
                losses[i] = values[1];
                remaining[i] = values[2];

                games[i] = new int[numTeams];
                for (int k = 0; k < numTeams; k++) {
                    games[i][k] = values[3 + k];
                }

                isTeamEliminated[i] = UNKNOWN;
                // certificates.add(i, null);
                certificates[i] = null;

            }

        } catch (IOException e) {

        }
    }

    private boolean isTrivialEliminated(String team) {
        /*
         * If the maximum number of games team x can win is less than the number
         * of wins of some other team i, then team x is trivially eliminated.
         * That is, if w[x] + r[x] < w[i], then team x is mathematically
         * eliminated.
         */

        int x = ix(team);

        int maxPossibleWins = wins[x] + remaining[x];

        return IntStream.range(0, wins.length).filter(i -> {
            if (i != x && maxPossibleWins < wins[i]) {
                // Vector<String> c = certificates.get(x);
                Vector<String> c = certificates[x];
                if (c == null) {
                    c = new Vector<String>();
                    // certificates.add(x, c);
                    certificates[x] = c;
                }
                c.add(xi(i));
                return true;
            }
            return false;
        }).count() > 0;
    }

    private boolean runFordFulkerson(String team) {

        int x = ix(team);
        int v = determineNumberVertices(x);

        FlowNetwork fn = buildFlowNetwork(x, v);
        FordFulkerson ff = new FordFulkerson(fn, 0, v - 1);

        Iterable<FlowEdge> iter = fn.adj(0);

        /*
         * If all edges in the maxflow that are pointing from s are full, then
         * this corresponds to assigning winners to all of the remaining games
         * in such a way that no team wins more games than x. If some edges
         * pointing from s are not full, then there is no scenario in which team
         * x can win the division.
         */
        boolean isEliminated = false;
        for (FlowEdge fe : iter) {
            if (fe.capacity() != fe.flow()) {
                isEliminated = true;
                break;
            }
        }

        if (isEliminated) {
            /*
             * In fact, when a team is mathematically eliminated there always
             * exists such a convincing certificate of elimination, where R is
             * some subset of the other teams in the division. Moreover, you can
             * always find such a subset R by choosing the team vertices on the
             * source side of a min s-t cut in the baseball elimination network.
             * Note that although we solved a maxflow/mincut problem to find the
             * subset R, once we have it, the argument for a team's elimination
             * involves only grade-school algebra.
             */

            /*
             * You can access the value of the flow with the value() method; you
             * can identify which vertices are on the source side of the mincut
             * with the inCut() method.
             */

            Vector<String> certificate = new Vector<String>();
            int numTeams = games.length;
            for (int i = 0; i < numTeams; i++) {

                if (i != x && ff.inCut(i)) {
                    certificate.addElement(xi(i));
                }
            }
            // certificates.add(x, certificate);
            certificates[x] = certificate;
        }

        return isEliminated;

    }

    private FlowNetwork buildFlowNetwork(int x, int numVertices) {

        FlowNetwork fn = new FlowNetwork(numVertices);

        int numTeams = games.length;
        int w = 1 + (numVertices - numTeams + 1 - 2); // index of the first
                                                      // "middle" vertex
        for (int i = 0; i < numTeams; i++) {
            if (i == x) {
                continue;
            }

            // boolean isHaveGame = false;

            for (int j = i + 1; j < numTeams; j++) {
                if (j == x) {
                    continue;
                }
                /*
                 * We connect an artificial source vertex s to each game vertex
                 * i-j and set its capacity to g[i][j]. If a flow uses all
                 * g[i][j] units of capacity on this edge, then we interpret
                 * this as playing all of these games, with the wins distributed
                 * between the team vertices i and j.
                 */
                int capacity = games[i][j];
                if (capacity > 0) {
                    // isHaveGame = true;
                    FlowEdge edgeStart = new FlowEdge(0, w, capacity);
                    fn.addEdge(edgeStart);

                    /*
                     * We connect each game vertex i-j with the two opposing
                     * team vertices to ensure that one of the two teams earns a
                     * win. We do not need to restrict the amount of flow on
                     * such edges.
                     */
                    FlowEdge edge_Game_ij_Team_i = new FlowEdge(w, i + 1,
                            Double.POSITIVE_INFINITY);
                    fn.addEdge(edge_Game_ij_Team_i);
                    FlowEdge edge_Game_ij_Team_j = new FlowEdge(w, j + 1,
                            Double.POSITIVE_INFINITY);
                    fn.addEdge(edge_Game_ij_Team_j);
                    w++;

                    // int maxAllowedWins = wins[x] + remaining[x] - wins[j];
                    // if (maxAllowedWins > 0) {
                    // FlowEdge edge_Team_i_sink = new FlowEdge(j + 1,
                    // numVertices - 1, maxAllowedWins);
                    // fn.addEdge(edge_Team_i_sink);
                    // }

                }
            }

            /*
             * Finally, we connect each team vertex to an artificial sink vertex
             * t. We want to know if there is some way of completing all the
             * games so that team x ends up winning at least as many games as
             * team i. Since team x can win as many as w[x] + r[x] games, we
             * prevent team i from winning more than that many games in total,
             * by including an edge from team vertex i to the sink vertex with
             * capacity w[x] + r[x] - w[i].
             */
            // if (isHaveGame) {
            int maxAllowedWins = wins[x] + remaining[x] - wins[i];
            if (maxAllowedWins > 0) {
                FlowEdge edge_Team_i_sink = new FlowEdge(i + 1, numVertices - 1,
                        maxAllowedWins);
                fn.addEdge(edge_Team_i_sink);
            }
            // }

        }

        return fn;

    }

    private int determineNumberVertices(int x) {

        int v = 0;
        int numTeams = games.length;
        for (int i = 0; i < numTeams; i++) {
            if (i == x) {
                continue;
            }

            for (int k = i; k < numTeams; k++) {
                if (k == x) {
                    continue;
                }

                v += games[i][k];
            }

        }

        return v + 2 + (numTeams - 1); // +2 for source and sink, +number of
                                       // teams, -1 for the team under question
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
