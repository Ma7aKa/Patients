package com.example.serge.myapplication;

import java.util.Comparator;

public class AlphabetComparator implements Comparator<Patient>
{
    @Override
    public int compare(Patient o1, Patient o2)
    {
        if (o1.getFullName().toLowerCase().compareTo(o2.getFullName().toLowerCase()) > 0) return 1;
        if (o1.getFullName().toLowerCase().compareTo(o2.getFullName().toLowerCase()) < 0) return -1;
        return 0;
    }
}
