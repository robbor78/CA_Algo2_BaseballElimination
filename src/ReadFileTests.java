import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReadFileTests {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        String[] filenames = {
                "/run/media/bert/280AC22E0AF59495/coursera/algorithms/2/assignments/3BaseballElimination/baseball/teams1.txt",
                "/run/media/bert/280AC22E0AF59495/coursera/algorithms/2/assignments/3BaseballElimination/baseball/teams4.txt" };

        String[][] names = { { "Turing" },
                { "Atlanta", "Philadelphia", "New_York", "Montreal" } };
        int[][] wins = { { 100 }, { 83, 80, 78, 77 } };
        int[][] losses = { { 55 }, { 71, 79, 78, 82 } };
        int[][] remaining = { { 0 }, { 8, 3, 6, 3 } };
        int[][][] games = { { { 0 } }, { { 0, 1, 6, 1 }, { 1, 0, 0, 2 },
                { 6, 0, 0, 0 }, { 1, 2, 0, 0 } } };

        int length = filenames.length;
        for (int i = 0; i < length; i++) {

            String filename = filenames[i];
            BaseballElimination be = new BaseballElimination(filename);

            TestEqualTeams(names[i], be.teams());
            for (int k = 0; k < names[i].length; k++) {
                TestEqual(wins[i][k], be.wins(names[i][k]));
                TestEqual(losses[i][k], be.losses(names[i][k]));
                TestEqual(remaining[i][k], be.remaining(names[i][k]));
                for (int j = 0; j < games[i][k].length; j++) {
                    TestEqual(games[i][k][j],
                            be.against(names[i][k], names[i][j]));
                }

            }

        }
    }

    private void TestEqual(int expected, int actual) {
        assertEquals(expected, actual);
    }

    private void TestEqualTeams(String[] strings, Iterable<String> teams) {

        Iterator<String> i1 = Arrays.asList(strings).iterator();

        while (i1.hasNext()) {
            Iterator<String> i2 = teams.iterator();
            
            String s1 = i1.next();

            boolean found = false;
            while (i2.hasNext() && !found) {
                found = s1.compareTo(i2.next()) == 0;
            }
            assertTrue(found);
        }

    }

}
