package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class procedureTest {
    procedure test1 = new procedure(1,"Sonogram","Echo", 15, 15.00f,1);
    procedure test2 = new procedure(1,"MRI","Knee", 30, 47.98f,1);
    procedure test3 = new procedure(1,"CT","Head", 45, 99.97f,1);

    @Test
    void getLength() {
        assertEquals(15,test1.getLength());
        assertEquals(30,test2.getLength());
        assertEquals(45,test3.getLength());
    }

    @Test
    void getCost() {
        assertEquals(15.00f,test1.getCost());
        assertEquals(47.98f,test2.getCost());
        assertEquals(99.97f,test3.getCost());
    }
}