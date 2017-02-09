package com.example.serge.myapplication;

import java.util.Comparator;

public class RegistrationDateComparator implements Comparator<Patient>
{
    @Override
    public int compare(Patient o1, Patient o2)
    {
        if(o1.getRegistration() > o2.getRegistration()) return -1;
        if(o1.getRegistration() < o2.getRegistration()) return 1;
        return 0;
    }
}
