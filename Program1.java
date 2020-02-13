/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.lang.reflect.Array;
import java.util.*;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */

    public int[][] locationPrefArray(Matching marriage)
    {
        int[][] ret = new int[marriage.getLocationCount()][marriage.getEmployeeCount()];

        for(int i=0; i<marriage.getLocationCount(); i++)
        {
            for(int j=0; j<marriage.getEmployeeCount(); j++)
            {
                ret[i][marriage.getLocationPreference().get(i).get(j)] = j;
            }
        }
        return ret;
    }

    public int[][] employeePrefArray(Matching marriage)
    {
        int[][] ret = new int[marriage.getEmployeeCount()][marriage.getLocationCount()];

        for(int i=0; i<marriage.getEmployeeCount(); i++)
        {
            for(int j=0; j<marriage.getLocationCount(); j++)
            {
                ret[i][marriage.getEmployeePreference().get(i).get(j)] = j;
            }
        }
        return ret;
    }

    @Override
    public boolean isStableMatching(Matching marriage) {
        // Check if there is matching first
        for(int i=0; i<marriage.getLocationSlots().size(); i++)
        {
            int num = marriage.getLocationSlots().get(i);
            for(int j=0; j<marriage.getEmployeeMatching().size(); j++)
            {
                if(marriage.getEmployeeMatching().get(j) == i)
                {
                    num--;
                }
            }
            if(num > 0)
            {
                return false;
            }
        }

        // First type of instability
        ArrayList<Integer> not_matched = new ArrayList<>();
        for(int i=0; i< marriage.getEmployeeCount(); i++)
        {
            if(marriage.getEmployeeMatching().get(i) == -1)
            {
                not_matched.add(i);
            }
        }
        for(int i=0; i < marriage.getLocationCount(); i++) {
            ArrayList<Integer> matched_employee_indices = new ArrayList<>();    // For this location
            for(int j=0; j < marriage.getEmployeeCount(); j++) {
                if(marriage.getEmployeeMatching().get(j) == i)
                {
                    matched_employee_indices.add(j);
                }
            }
            for(int j=0; j<matched_employee_indices.size(); j++)
            {
                int employee_pref = marriage.getLocationPreference().get(i).indexOf(matched_employee_indices.get(j));
                for(int k=0; k<employee_pref; k++)
                {
                    if(not_matched.indexOf(marriage.getLocationPreference().get(i).get(k)) != -1)    // Means one of the employees which wasn't matched at all was preferable to the current employee
                    {
                        return false;
                    }
                }
            }
        }

        // Second type of instability

        for(int i=0; i < marriage.getLocationCount(); i++) {
            ArrayList<Integer> matched_employee_indices = new ArrayList<>();    // For this location
            for(int j=0; j < marriage.getEmployeeCount(); j++) {
                if(marriage.getEmployeeMatching().get(j) == i)
                {
                    matched_employee_indices.add(j);
                }
            }
            for(int j=0; j<matched_employee_indices.size(); j++) {
                int employee = matched_employee_indices.get(j);
                // Check all stores that employee at j prefers to the current store
                for(int k=0; k < marriage.getEmployeePreference().get(employee).indexOf(i); k++)    // i is the store employee was matched to
                {
                    int kth_store = marriage.getEmployeePreference().get(employee).get(k);
                    // Go through all the employees store k has and check if it prefers employee over anyone
                    ArrayList<Integer> store_k_matching = new ArrayList<>();
                    for(int m=0; m<marriage.getEmployeeMatching().size(); m++)
                    {
                        if(marriage.getEmployeeMatching().get(m) == kth_store)
                        {
                            store_k_matching.add(m);
                        }
                    }
                    for(int m=0; m < store_k_matching.size(); m++)
                    {
                        if(marriage.getLocationPreference().get(kth_store).indexOf(store_k_matching.get(m)) > marriage.getLocationPreference().get(kth_store).indexOf(employee)) // if current matched employee is later in pref list than employee idx
                        {
                            return false;
                        }
                    }
                }
            }

        }

        return true;
    }


    /**
     * Determines a employee optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_employeeoptimal(Matching marriage) {

        int[] employee_last_asked = new int[marriage.getEmployeeCount()];
        Arrays.fill(employee_last_asked, -1);
        boolean[] employee_matched = new boolean[marriage.getEmployeeCount()];
        Arrays.fill(employee_matched, false);

        ArrayList<ArrayList<Integer>> store_matching = new ArrayList<>();
        for(int i=0; i<marriage.getLocationCount(); i++)
        {
            store_matching.add(new ArrayList<Integer>());
            for(int j=0; j<marriage.getLocationSlots().get(i); j++)
            {
                store_matching.get(i).add(-1);
            }
        }


        boolean flag = ((marriage.getEmployeeCount() > 0) & (marriage.getLocationCount() > 0));
        while(flag)
        {
            int employee = -1;  // Employee who proposes to their favorite store
            for(int i=0; i<marriage.getEmployeeCount(); i++)
            {
                if((!employee_matched[i]) & (employee_last_asked[i] != marriage.getEmployeePreference().get(i).get(marriage.getEmployeePreference().get(i).size()-1)))  // continue loop
                {
                    employee = i;
                    break;
                }
            }
            int ask_store = 0;
            int temp = 0;
            if(employee_last_asked[employee] == -1)
            {
                ask_store = marriage.getEmployeePreference().get(employee).get(0);
            }
            else {
                temp = marriage.getEmployeePreference().get(employee).indexOf(employee_last_asked[employee]);
                ask_store = marriage.getEmployeePreference().get(employee).get(temp+1);
            }
            employee_last_asked[employee] = ask_store;

            // if store has an empty spot
            boolean empty_spot = false;
            int empty_idx = -1;
            for(int i=0; i<store_matching.get(ask_store).size(); i++)
            {
                if(store_matching.get(ask_store).get(i) == -1)
                {
                    empty_spot = true;
                    empty_idx = i;
                    break;
                }
            }
            if(empty_spot)
            {
                store_matching.get(ask_store).set(empty_idx, employee);
                employee_matched[employee] = true;
            }
            else
            {
                int worst_employee_pref = Integer.MIN_VALUE;
                int worst_employee_idx = -1;
                for(int i=0; i<store_matching.get(ask_store).size(); i++)
                {
                    int pref = marriage.getLocationPreference().get(ask_store).indexOf(store_matching.get(ask_store).get(i));
                    if(worst_employee_pref < pref) {
                        worst_employee_pref = pref;
                        worst_employee_idx = i;
                    }
                }
                int employee_pref = marriage.getLocationPreference().get(ask_store).indexOf(employee);
                if(employee_pref < worst_employee_pref)
                {
                    employee_matched[store_matching.get(ask_store).get(worst_employee_idx)] = false;    // TODO: HAD ISSUES HERE
                    employee_matched[employee] = true;
                    store_matching.get(ask_store).set(worst_employee_idx, employee);
                }
            }
            // while condition update
            flag = false;
            for(int i=0; i<marriage.getEmployeeCount(); i++)
            {
                if((!employee_matched[i]) & (employee_last_asked[i] != marriage.getEmployeePreference().get(i).get(marriage.getEmployeePreference().get(i).size()-1)))  // continue loop
                {
                    flag = true;
                    break;
                }
            }
//            for(int i=0; i<store_matching.size(); i++)
//            {
//                for(int j=0; j<store_matching.get(i).size(); j++)
//                {
//                    if(store_matching.get(i).get(j) == -1)
//                    {
//                        flag = true;
//                        break;
//                    }
//                }
//            }

        }
        int[] ret_arr = new int[marriage.getEmployeeCount()];
        Arrays.fill(ret_arr, -1);

        for(int i=0; i<store_matching.size(); i++)
        {
            for(int j=0; j<store_matching.get(i).size(); j++)
            {
                if(store_matching.get(i).get(j) != -1)
                {
                    ret_arr[store_matching.get(i).get(j)] = i;
                }
            }
        }

        ArrayList<Integer> ret = new ArrayList<>();
        for(int i=0; i<marriage.getEmployeeCount(); i++)
        {
            ret.add(ret_arr[i]);
        }

        return new Matching(marriage.getLocationCount(), marriage.getEmployeeCount(), marriage.getLocationPreference(), marriage.getEmployeePreference(), marriage.getLocationSlots(), ret);

    }

    /**
     * Determines a location optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_locationoptimal(Matching marriage) {

        int[] store_last_asked = new int[marriage.getEmployeeCount()];
        Arrays.fill(store_last_asked, -1);
        boolean[] employee_matched = new boolean[marriage.getEmployeeCount()];
        Arrays.fill(employee_matched, false);

        boolean flag = ((marriage.getEmployeeCount() > 0) & (marriage.getLocationCount() > 0));

        ArrayList<ArrayList<Integer>> store_matching = new ArrayList<>();
        for(int i=0; i<marriage.getLocationCount(); i++)
        {
            store_matching.add(new ArrayList<Integer>());
            for(int j=0; j<marriage.getLocationSlots().get(i); j++)
            {
                store_matching.get(i).add(-1);
            }
        }




        while(flag)
        {
            int store_num = -1;
            int empty_idx = -1;
            for(int i=0; i<store_matching.size(); i++)
            {
                for(int j=0; j<store_matching.get(i).size(); j++)
                {
                    if(store_matching.get(i).get(j) == -1)
                    {
                        store_num = i;
                        empty_idx = j;
                        break;
                    }
                }
            }

            int employee = -1;
            if(store_last_asked[store_num] == -1)
            {
                employee = marriage.getLocationPreference().get(store_num).get(0);
            }
            else
            {
                int temp = marriage.getLocationPreference().get(store_num).indexOf(store_last_asked[store_num]);
                employee = marriage.getLocationPreference().get(store_num).get(temp+1);
            }

            store_last_asked[store_num] = employee;

            if(!employee_matched[employee])
            {
                store_matching.get(store_num).set(empty_idx, employee);
                employee_matched[employee] = true;
            }
            else
            {
                int store_employee_is_with = -1;
                for(int i=0; i<store_matching.size(); i++)
                {
                    for(int j=0; j<store_matching.get(i).size(); j++)
                    {
                        if(store_matching.get(i).get(j) == employee)
                        {
                            store_employee_is_with = i;
                            break;
                        }
                    }
                }
                int current_pref = marriage.getEmployeePreference().get(employee).indexOf(store_employee_is_with);
                int possible_pref =  marriage.getEmployeePreference().get(employee).indexOf(store_num);

                if(current_pref > possible_pref)    // Prefers new store
                {
                    store_matching.get(store_employee_is_with).set(store_matching.get(store_employee_is_with).indexOf(employee), -1);
                    store_matching.get(store_num).set(empty_idx, employee);
                    employee_matched[employee] = true;
                }

            }



            flag = false;
            //while condition update
            for(int i=0; i<store_matching.size(); i++)
            {
                for(int j=0; j<store_matching.get(i).size(); j++)
                {
                    if(store_matching.get(i).get(j) == -1)
                    {
                        flag = true;
                        break;
                    }
                }
            }
        }
        int[] ret_arr = new int[marriage.getEmployeeCount()];
        Arrays.fill(ret_arr, -1);

        for(int i=0; i<store_matching.size(); i++)
        {
            for(int j=0; j<store_matching.get(i).size(); j++)
            {
                if(store_matching.get(i).get(j) != -1)
                {
                    ret_arr[store_matching.get(i).get(j)] = i;
                }
            }
        }

        ArrayList<Integer> ret = new ArrayList<>();
        for(int i=0; i<marriage.getEmployeeCount(); i++)
        {
            ret.add(ret_arr[i]);
        }

        return new Matching(marriage.getLocationCount(), marriage.getEmployeeCount(), marriage.getLocationPreference(), marriage.getEmployeePreference(), marriage.getLocationSlots(), ret);


    }

//    @Override
//    public boolean is_employee_optimal(Matching marriage) {
//
//        // Approach: 1. Create all possible matchings
//        //           2. If matching is stable, check if any of the employees are matched to better stores, return false if found
//
//        ArrayList<Matching> all_matchings = new ArrayList<>();
//
//        int total_slots = 0;
//        for(int i=0; i<marriage.getLocationCount(); i++)
//        {
//            total_slots += marriage.getLocationSlots().get(i);
//        }
//
////        int perm = 1;
////
////
////        for(int i= 1; i<marriage.getEmployeeCount() + 1; i++)
////        {
////            perm *= i;
////            System.out.println(perm);
////        }
//
//
//        for(int i=0; i < 10000000; i++)
//        {
//            Integer[] employee_matching = new Integer[marriage.getEmployeeCount()];
//
//            for(int j=0; j<marriage.getEmployeeCount(); j++)
//            {
//                employee_matching[j] = j;
//            }
//
//            List<Integer> temp = Arrays.asList(employee_matching);
//
//            Collections.shuffle(temp);
//
//            temp.toArray(employee_matching);
//
//            // Map index to location / -1
//
//            ArrayList<Integer> actual_matching = new ArrayList<>();
//
//            for(int j=0; j<marriage.getEmployeeCount(); j++)
//            {
//                int val = employee_matching[j];
//                boolean flag = true;
//                for(int k=0; k<marriage.getLocationCount(); k++)
//                {
//                    val -= marriage.getLocationSlots().get(k);
//                    if(val < 0)
//                    {
//                        actual_matching.add(k);
//                        flag = false;
//                        break;
//                    }
//                }
//                if(flag)
//                {
//                    actual_matching.add(-1);
//                }
//            }
//
//            Matching to_test = new Matching(marriage.getLocationCount(), marriage.getEmployeeCount(), marriage.getLocationPreference(), marriage.getEmployeePreference(), marriage.getLocationSlots(), actual_matching);
//
//
//            // Run isStable()
//
//            boolean is_stable = isStableMatching(to_test);
//
//            // if true, check if optimal
//
//            if(is_stable) {
//                System.out.println("ez");
//            }
//        }
//
//
//
//
//        return true;
//    }
}
