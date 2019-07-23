package com.rendersoncs.reportform.extension;

public class StringExtension {

    public static String limitsText(String text, int i) {
        if (text.length() > i) {
            int startIndex = 0;
            StringBuilder stringBuilder = new StringBuilder();
            String textSize = text.substring(startIndex, i);
            return stringBuilder.append(textSize).append("...").toString();
        } else {
            return text;
        }
    }
}
