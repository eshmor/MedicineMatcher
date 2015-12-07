package com.rdln.medicinematcher;

import android.app.Application;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
//@RunWith(AndroidTestCase.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void parseText(){
        ParseXml px = new ParseXml();
        try {
            px.parse("<Res><PatientName>Matan Alph1</PatientName><MedecineName>אקמול</MedecineName><IsAllowed>false</IsAllowed></Res>");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}