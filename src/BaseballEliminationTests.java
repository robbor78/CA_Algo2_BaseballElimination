import static org.junit.Assert.*;

import java.util.ArrayList;
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
                "/run/media/bert/280AC22E0AF59495/coursera/algorithms/2/assignments/3BaseballElimination/baseball/teams4.txt",
                "/run/media/bert/280AC22E0AF59495/coursera/algorithms/2/assignments/3BaseballElimination/baseball/teams5.txt" };
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

        isEliminated[1] = new HashMap<>();
        isEliminated[1].put("New_York", false);
        isEliminated[1].put("Baltimore", false);
        isEliminated[1].put("Boston", false);
        isEliminated[1].put("Toronto", false);
        isEliminated[1].put("Detroit", true);

        certificates[1] = new HashMap<>();
        tmp = new Vector<>();
        tmp.addElement("New_York");
        tmp.addElement("Baltimore");
        tmp.addElement("Boston");
        tmp.addElement("Toronto");
        certificates[1].put("Detroit", tmp);

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
        String[] cert = toArray(actualCert);
        int length = cert.length;
        for (int i = 0; i < length; i++) {
            String a = cert[i];
            r++;
            wins += be.wins(a);
            for (int j = i + 1; j < length; j++) {
                String b = cert[j];
                g += be.against(a, b);
            }
        }
        double ar = ((double) (wins + g)) / (double) (r);
        int max = be.wins(team) + be.remaining(team);
        assertTrue(ar > (double) max);
    }

    private void TestEqual(Iterable<String> expected, Iterable<String> actual) {

        TestEqualOneWay(expected, actual);
        TestEqualOneWay(actual, expected);

    }

    private String[] toArray(final Iterable<String> elements) {
        ArrayList<String> al = new ArrayList<String>();
        for (String element : elements) {
            al.add(element);
        }

        return al.toArray(new String[al.size()]);
        // String t[] = new String[length];
        // int i = 0;
        // for (final String element : elements) {
        // t[i++] = element;
        // }
        // return t;
    }

    private void TestEqualOneWay(Iterable<String> expected,
            Iterable<String> actual) {
        for (String e : expected) {
            boolean isFound = false;
            for (String a : actual) {
                isFound = e.compareTo(a) == 0;
                if (isFound) {
                    break;
                }
            }
            assertTrue(isFound);
        }
    }

}
