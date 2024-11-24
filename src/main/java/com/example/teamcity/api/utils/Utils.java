package com.example.teamcity.api.utils;

public class Utils {

    public static String createBuildTypeLocator(String projectId, String buildTypeName) {
        StringBuilder formattedBuildTypeName = new StringBuilder();

        boolean capitalizeNext = true;

        // Iterate over each character in the buildTypeName
        for (char ch : buildTypeName.toCharArray()) {
            if (ch == '_') {
                capitalizeNext = true; // Set flag to capitalize the next character
            } else {
                if (capitalizeNext) {
                    formattedBuildTypeName.append(Character.toUpperCase(ch));
                    capitalizeNext = false;
                } else {
                    formattedBuildTypeName.append(ch);
                }
            }
        }

        return projectId + "_" + formattedBuildTypeName;
    }
}