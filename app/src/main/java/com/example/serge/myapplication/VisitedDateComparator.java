package com.example.serge.myapplication;

import java.util.Comparator;

public class VisitedDateComparator implements Comparator<Patient>
{

    @Override
    public int compare(Patient o1, Patient o2)
    {
        if (o1.getLastVisit() < o2.getLastVisit()) return 1;
        if (o1.getLastVisit() > o2.getLastVisit()) return -1;
        return 0;
    }
}
