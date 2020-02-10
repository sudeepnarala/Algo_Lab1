import java.util.ArrayList;

/**
 * A Matching represents a candidate solution to the Stable Marriage problem. A Matching may or may
 * not be stable.
 */
public class Matching {
    /**
     * Number of locations.
     */
    private Integer m;

    /**
     * Number of employees.
     */
    private Integer n;

    /**
     * A list containing each location's preference list.
     */
    private ArrayList<ArrayList<Integer>> location_preference;

    /**
     * A list containing each employee's preference list.
     */
    private ArrayList<ArrayList<Integer>> employee_preference;

    /**
     * Number of slots available in each location.
     */
    private ArrayList<Integer> location_slots;

    /**
     * Matching information representing the index of location a employee is matched to, -1 if not
     * matched.
     *
     * <p>An empty matching is represented by a null value for this field.
     */
    private ArrayList<Integer> employee_matching;

    public Matching(
            Integer m,
            Integer n,
            ArrayList<ArrayList<Integer>> location_preference,
            ArrayList<ArrayList<Integer>> employee_preference,
            ArrayList<Integer> location_slots) {
        this.m = m;
        this.n = n;
        this.location_preference = location_preference;
        this.employee_preference = employee_preference;
        this.location_slots = location_slots;
        this.employee_matching = null;
    }

    public Matching(
            Integer m,
            Integer n,
            ArrayList<ArrayList<Integer>> location_preference,
            ArrayList<ArrayList<Integer>> employee_preference,
            ArrayList<Integer> location_slots,
            ArrayList<Integer> employee_matching) {
        this.m = m;
        this.n = n;
        this.location_preference = location_preference;
        this.employee_preference = employee_preference;
        this.location_slots = location_slots;
        this.employee_matching = employee_matching;
    }

    /**
     * Constructs a solution to the stable marriage problem, given the problem as a Matching. Take a
     * Matching which represents the problem data with no solution, and a employee_matching which
     * solves the problem given in data.
     *
     * @param data              The given problem to solve.
     * @param employee_matching The solution to the problem.
     */
    public Matching(Matching data, ArrayList<Integer> employee_matching) {
        this(
                data.m,
                data.n,
                data.location_preference,
                data.employee_preference,
                data.location_slots,
                employee_matching);
    }

    /**
     * Creates a Matching from data which includes an empty solution.
     *
     * @param data The Matching containing the problem to solve.
     */
    public Matching(Matching data) {
        this(
                data.m,
                data.n,
                data.location_preference,
                data.employee_preference,
                data.location_slots,
                new ArrayList<Integer>(0));
    }

    public void setEmployeeMatching(ArrayList<Integer> employee_matching) {
        this.employee_matching = employee_matching;
    }

    public Integer getLocationCount() {
        return m;
    }

    public Integer getEmployeeCount() {
        return n;
    }

    public ArrayList<ArrayList<Integer>> getLocationPreference() {
        return location_preference;
    }

    public ArrayList<ArrayList<Integer>> getEmployeePreference() {
        return employee_preference;
    }

    public ArrayList<Integer> getLocationSlots() {
        return location_slots;
    }

    public ArrayList<Integer> getEmployeeMatching() {
        return employee_matching;
    }

    public int totalLocationSlots() {
        int slots = 0;
        for (int i = 0; i < m; i++) {
            slots += location_slots.get(i);
        }
        return slots;
    }

    public String getInputSizeString() {
        return String.format("m=%d n=%d\n", m, n);
    }

    public String getSolutionString() {
        if (employee_matching == null) {
            return "";
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < employee_matching.size(); i++) {
            String str = String.format("Employee %d Location %d", i, employee_matching.get(i));
            s.append(str);
            if (i != employee_matching.size() - 1) {
                s.append("\n");
            }
        }
        return s.toString();
    }

    public String toString() {
        return getInputSizeString() + getSolutionString();
    }
}
