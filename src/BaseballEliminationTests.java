import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class BaseballEliminationTests {

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
                "/run/media/bert/280AC22E0AF59495/coursera/algorithms/2/assignments/3BaseballElimination/baseball/teams4.txt" };
        int length = filenames.length;

        HashMap<String, Boolean> isEliminated[] = (HashMap<String, Boolean>[]) new HashMap[length];
        isEliminated[0] = new HashMap<>();
        isEliminated[0].put("Atlanta", false);
        isEliminated[0].put("Philadelphia", true);
        isEliminated[0].put("New_York", false);
        isEliminated[0].put("Montreal", true);

        HashMap<String, Vector<String>>[] certificates = (HashMap<String, Vector<String>>[]) new HashMap[length];
        certificates[0] = new HashMap<>();
        Vector<String> tmp = new Vector<>();
        tmp.addElement("Atlanta");
        certificates[0].put("Montreal", tmp);
        tmp = new Vector<>();
        tmp.addElement("Atlanta");
        tmp.addElement("New_York");
        certificates[0].put("Philadelphia", tmp);

        for (int i = 0; i < length; i++) {

            String filename = filenames[i];
            BaseballElimination be = new BaseballElimination(filename);

            for (String team : be.teams()) {
                boolean actual = be.isEliminated(team);
                assertEquals(isEliminated[i].get(team), actual);

                if (actual) {
                    Iterable<String> expectedCert = certificates[i].get(team);
                    Iterable<String> actualCert = be
                            .certificateOfElimination(team);

                    validateCertificate(be, team, actualCert);

                    TestEqual(expectedCert, actualCert);
                }
            }

        }
    }

    private void validateCertificate(BaseballElimination be, String team,
            Iterable<String> actualCert) {
        int wins = 0;
        int g = 0;
        int r = 0;
        for (String a : actualCert) {
            r++;
            wins += be.wins(a);
            for (String b : actualCert) {
                if (a.compareTo(b) == 0) {
                    continue;
                }
                g += be.against(a, b);
            }
        }
        double ar = ((double) (wins + g)) / (double) (r);
        int max = be.wins(team) + be.remaining(team);
        assertTrue(ar > (double) max);
    }

    private void TestEqual(Iterable<String> expected, Iterable<String> actual) {

        for (String e : expected) {
            for (String a : actual) {
                if (e.compareTo(a) == 0) {
                    break;
                }
            }
            assertTrue(false);
        }

        for (String a : expected) {
            for (String e : actual) {
                if (e.compareTo(a) == 0) {
                    break;
                }
            }
            assertTrue(false);
        }

    }

}
