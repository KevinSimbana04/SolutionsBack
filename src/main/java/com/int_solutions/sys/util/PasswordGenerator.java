package com.int_solutions.sys.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class PasswordGenerator {

    private static final String mayusculas ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String minusculas= "abcdefghijklmnopqrstuvwxyz";
    private static final String numeros= "0123456789";
    private static final String caracteres="!@#$%^&*()-_=+";
    private static final String todos= mayusculas+minusculas+numeros+caracteres;

    private static final SecureRandom random =new SecureRandom();

    public static String generarpassword(int longitud){
        StringBuilder password = new StringBuilder(longitud);

        password.append(mayusculas.charAt(random.nextInt(mayusculas.length())));
        password.append(minusculas.charAt(random.nextInt(minusculas.length())));
        password.append(numeros.charAt(random.nextInt(numeros.length())));
        password.append(caracteres.charAt(random.nextInt(caracteres.length())));

        for(int i =4 ; i< longitud;i++){
            password.append(todos.charAt(random.nextInt(todos.length())));
        }

        return password.toString();
    }
}
