package com.WelfenHub.services;

/**
 * Creates a random token for a random link
 */
public class PasswordResetLinkService {

    /**
     * Private constructor -> no instance can be created
     */
    PasswordResetLinkService(){}

    /**
     * Creates a random String
     * @param n length of the random String
     * @return random token as a String
     */
    private static String randomToken(int n) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "123456789" + "abcdefghijklmnopqrstuvwxyz";

        StringBuilder tokenBuilder = new StringBuilder();

        for (int i = 0; i < n; i++) {
            int index = (int)(alphaNumericString.length() * Math.random());

            tokenBuilder.append(alphaNumericString.charAt(index));
        }

        return tokenBuilder.toString();
    }

    /**
     * Creates a random link for password reset
     * @return random link with random token
     */
    public static String linkGenerator() {
        return "http://localhost:8080/reset-password?token=" + randomToken(30);
    }
}
