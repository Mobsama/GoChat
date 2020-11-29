package com.mob.gochat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {
    public static boolean isEmailValid(String email){
        boolean isValid = false;
        String expression = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\.[a-z]{2,}$";
        CharSequence inputEmail = email;

        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputEmail);
        if(matcher.matches()){
            isValid = true;
        }

        return isValid;
    }
}
