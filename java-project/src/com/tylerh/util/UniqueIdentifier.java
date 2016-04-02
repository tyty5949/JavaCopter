package com.tylerh.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tyler on 3/3/14.
 * Project: Network
 */
public class UniqueIdentifier {

    /* The range of the number of clients possible */
    public static final int RANGE = 10000;

    /* The list of IDs */
    private static List<Integer> ids = new ArrayList<Integer>();

    /* The current index in the list */
    private static int index;

    /**
     * Static call to initialize the ID list
     * Shuffles the array so when index is changed it is a unique number
     */
    static {
        index = 0;
        for (int i = 0; i < RANGE; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);
    }

    /**
     * Constructor to MAKE SURE that this class cannot be instantiated
     */
    private UniqueIdentifier() {
    }

    /**
     * Method to get a unique number from the list
     *
     * @return - The number
     */
    public static int getIdentifier() {
        if (index > ids.size() - 1) index = 0;
        return ids.get(index++);
    }
}