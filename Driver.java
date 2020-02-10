import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
    public static String filename;
    public static boolean testGS_l;
    public static boolean testGS_e;

    public static void main(String[] args) throws Exception {
        parseArgs(args);

        Matching problem = parseMatchingProblem(filename);
        testRun(problem);
    }

    private static void usage() {
        System.err.println("usage: java Driver [-gl] [-ge] <filename>");
        System.err.println("\t-gl\tTest Gale-Shapley location optimal implementation");
        System.err.println("\t-ge\tTest Gale-Shapley employee optimal implementation");
        System.exit(1);
    }

    public static void parseArgs(String[] args) {
        if (args.length == 0) {
            usage();
        }

        filename = "";
        testGS_l = false;
        testGS_e = false;
        boolean flagsPresent = false;

        for (String s : args) {
            if (s.equals("-gl")) {
                flagsPresent = true;
                testGS_l = true;
            } else if (s.equals("-ge")) {
                flagsPresent = true;
                testGS_e = true;
            } else if (!s.startsWith("-")) {
                filename = s;
            } else {
                System.err.printf("Unknown option: %s\n", s);
                usage();
            }
        }
        if (!flagsPresent) {
            testGS_l = true;
            testGS_e = true;
        }
    }

    public static Matching parseMatchingProblem(String inputFile) throws Exception {
        int m = 0;
        int n = 0;
        ArrayList<ArrayList<Integer>> locationPrefs, employeePrefs;
        ArrayList<Integer> locationSlots;

        Scanner sc = new Scanner(new File(inputFile));
        String[] inputSizes = sc.nextLine().split(" ");

        m = Integer.parseInt(inputSizes[0]);
        n = Integer.parseInt(inputSizes[1]);
        locationSlots = readSlotsList(sc, m);
        locationPrefs = readPreferenceLists(sc, m);
        employeePrefs = readPreferenceLists(sc, n);

        Matching problem = new Matching(m, n, locationPrefs, employeePrefs, locationSlots);

        return problem;
    }

    private static ArrayList<Integer> readSlotsList(Scanner sc, int m) {
        ArrayList<Integer> locationSlots = new ArrayList<Integer>(0);

        String[] slots = sc.nextLine().split(" ");
        for (int i = 0; i < m; i++) {
            locationSlots.add(Integer.parseInt(slots[i]));
        }

        return locationSlots;
    }

    private static ArrayList<ArrayList<Integer>> readPreferenceLists(Scanner sc, int m) {
        ArrayList<ArrayList<Integer>> preferenceLists;
        preferenceLists = new ArrayList<ArrayList<Integer>>(0);

        for (int i = 0; i < m; i++) {
            String line = sc.nextLine();
            String[] preferences = line.split(" ");
            ArrayList<Integer> preferenceList = new ArrayList<Integer>(0);
            for (Integer j = 0; j < preferences.length; j++) {
                preferenceList.add(Integer.parseInt(preferences[j]));
            }
            preferenceLists.add(preferenceList);
        }

        return preferenceLists;
    }

    public static void testRun(Matching problem) {
        Program1 program = new Program1();
        boolean isStable;

        if (testGS_l) {
            Matching GSMatching = program.stableMarriageGaleShapley_locationoptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley Location Optimal", isStable);
            System.out.println();
        }

        if (testGS_e) {
            Matching GSMatching = program.stableMarriageGaleShapley_employeeoptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley Employee Optimal", isStable);
            System.out.println();
        }

    }
}
