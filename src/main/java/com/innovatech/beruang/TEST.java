package com.innovatech.beruang;

import java.util.Calendar;
import java.util.Date;

public class TEST {
    public static long fixedtime = 1730091600000L;

    public TEST(boolean showeval){
        if (showeval){
            fixedtime = 1730437200000L;//1 nov
        } else {
            fixedtime = 1730091600000L;//28 octo
        }
    }

    public Calendar getcurrenttime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fixedtime);
        return cal;
    }

    public Date getcurrentdate(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fixedtime);
        return cal.getTime();
    }
}
