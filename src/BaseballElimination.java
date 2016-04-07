import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private HashMap<String, Integer> names;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] games;

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
        boolean isEliminated = isTrivialEliminated(team);
        if (!isEliminated) {
            runFordFulkerson(team);
        }
        return isEliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return null;
    }

    private int ix(String team) {
        if (!names.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return names.get(team);
    }

    private void parseFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            int numTeams = Integer.parseInt(line);
            names = new HashMap<String, Integer>();
            wins = new int[numTeams];
            losses = new int[numTeams];
            remaining = new int[numTeams];
            games = new int[numTeams][];

            for (int i = 0; i < numTeams; i++) {
                line = br.readLine();
                names.put(line.split("\\s+")[0], i);
                int[] values = Arrays.stream(line.split("\\s+")).skip(1)
                        .mapToInt(Integer::parseInt).toArray();

                wins[i] = values[0];
                losses[i] = values[1];
                remaining[i] = values[2];

                games[i] = new int[numTeams];
                for (int k = 0; k < numTeams; k++) {
                    games[i][k] = values[3 + k];
                }

            }

        } catch (IOException e) {

        }
    }

    private boolean isTrivialEliminated(String team) {
        int x = ix(team);

        int maxWins = wins[x] + remaining[x];

        return IntStream.range(0, wins.length)
                .filter(i -> i != x && wins[i] > maxWins).count() > 0;
    }

    private void runFordFulkerson(String team) {

        int x = ix(team);

        int v = determineV(x);

        FlowNetwork fn = new FlowNetwork(v);
        FordFulkerson ff = new FordFulkerson(fn, 0, v - 1);
        
        ff.

    }

    private int determineV(int x) {

        int v = 0;
        int length = games.length;
        for (int i = 0; i < length; i++) {
            if (i == x) {
                continue;
            }

            for (int k = i; k < length; k++) {
                if (k == x) {
                    continue;
                }

                v += games[i][k];
            }

        }

        return v;
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
