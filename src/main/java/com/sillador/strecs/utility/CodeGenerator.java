package com.sillador.strecs.utility;

import java.time.Year;
import java.util.Random;

public class CodeGenerator {


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 8;

    public static String generateCode() {
//        String year = String.valueOf(Year.now().getValue());
        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPart.append(CHARACTERS.charAt(index));
        }

        return randomPart.toString();
    }

}
