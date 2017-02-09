package com.example.serge.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Patient implements Serializable
{
    private String firstname;
    private String lastname;
    private String fullname;
    private String patronymic;
    private String  birth;
    private String number1;
    private String number2;
    private String number3;
    private String address;
    private ArrayList<Date> nextvisits;
    private ArrayList<Date> wasvisits;
    private ArrayList<String> descriptions;
    private long registration;
    private long last_visit;
    private byte [] bytes;
    private long SERIALIZE_ID;
    private int index;

    public Patient(String firstname, String lastname, String patronymic)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.fullname = firstname + " " + lastname;
        nextvisits = new ArrayList<>();
        wasvisits = new ArrayList<>();
        descriptions = new ArrayList<>();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        this.fullname = firstname + " " + lastname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        this.fullname = firstname + " " + lastname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String  birth) {
        this.birth = birth;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getNumber3() {
        return number3;
    }

    public void setNumber3(String number3) {
        this.number3 = number3;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(ArrayList<String> descriptions)
    {
        this.descriptions = new ArrayList<>();
        this.descriptions.addAll(descriptions);
    }

    public String getFullName() {
        return fullname;
    }

    public long getRegistration() {
        return registration;
    }

    public void setRegistration(long registration) {
        this.registration = registration;
    }

    public long getLastVisit() {
        return last_visit;
    }

    public void setLast_visit(long last_visit) {
        this.last_visit = last_visit;
    }

    public long getSERIALIZE_ID() {
        return SERIALIZE_ID;
    }

    public void setSERIALIZE_ID(long SERIALIZE_ID) {
        this.SERIALIZE_ID = SERIALIZE_ID;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean hasFirstName()
    {
        return firstname != null;
    }
    public boolean hasLastName()
    {
        return lastname != null;
    }
    public boolean hasPatronymic()
    {
        return patronymic != null;
    }
    public boolean hasBirth()
    {
        return birth != null;
    }
    public boolean hasNumber1()
    {
        return number1 != null;
    }
    public boolean hasNumber2()
    {
        return number2 != null;
    }
    public boolean hasNumber3()
    {
        return number3 != null;
    }
    public boolean hasAddress()
    {
        return address != null;
    }
    public boolean hasDescriptions()
    {
        return descriptions.size() > 0;
    }

    public void addNextVisitDate(Date date)
    {
        nextvisits.add(date);
    }
    public void removeNextVisitDate(Date date)
    {
        nextvisits.remove(date);
    }
    public void addDescription(String description)
    {
        descriptions.add(description);
    }
    public void removeDescription(int position)
    {
        descriptions.remove(position);
    }
    public void removeWasVisits(int position)
    {
        wasvisits.remove(position);
    }

    public boolean hasNextVisits()
    {
        return nextvisits.size() > 0;
    }

    public ArrayList<Date> getNextVisits()
    {
        return nextvisits;
    }

    public ArrayList<Date> getWasVisits()
    {
        return wasvisits;
    }

    public void setNextVisits(ArrayList<Date> nextvisits)
    {
        this.nextvisits = new ArrayList<>();
        this.nextvisits.addAll(nextvisits);
    }

    public void setWasVisits(ArrayList<Date> wasvisits)
    {
        this.wasvisits = new ArrayList<>();
        this.wasvisits.addAll(wasvisits);
    }

    public void addWasVisitDate(Date date)
    {
        wasvisits.add(0,date);
    }

    public boolean hasWasVisits()
    {
        return wasvisits.size() > 0;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
