package org.femtoframework.service;

import org.junit.Test;

import java.util.Locale;


public class NoSuchActionExceptionTest {

    @Test
    public void testMessage() {
        NoSuchActionException noSuchActionException = new NoSuchActionException("component", "action");
        System.out.println(noSuchActionException.getLocalizedMessage());
        System.out.println(noSuchActionException.getLocalizedMessage(Locale.CHINA));
    }
}