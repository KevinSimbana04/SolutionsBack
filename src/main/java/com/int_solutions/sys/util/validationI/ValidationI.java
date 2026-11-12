package com.int_solutions.sys.util.validationI;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;

public class ValidationI implements ConstraintValidator<IdentificacionE,String> {
    @Override
    public boolean isValid(String identificacion , ConstraintValidatorContext context){
        if(identificacion== null || identificacion.length()!=10){
            return  false;
        }
        if(!identificacion.matches("\\d{10}")){
            return false;
        }

        try{
            int acumulador = 0;
            int decimo_valor = 0;
            int valor_validacion = 0;
            ArrayList<Integer> lista = new ArrayList<>();

            for (int i = 0; i < 10 ; i++) {
                char caracter = identificacion.charAt(i);
                int numero = Character.getNumericValue(caracter);
                lista.add(numero);
            }

            for (int i = 0; i < 10; i++) {
                if((i/2)*2 == i){
                    int valor_doble = lista.get(i) * 2;
                    lista.set(i, valor_doble);

                    if(lista.get(i) >= 10){
                        int nuevo_valor = lista.get(i) - 9;
                        lista.set(i, nuevo_valor);
                    }
                }
            }

            for (int i = 0; i < 9; i++) {
                acumulador += lista.get(i);
            }

            while(decimo_valor < acumulador){
                decimo_valor = decimo_valor + 10;
            }
            valor_validacion = decimo_valor - acumulador;
            return lista.get(9) == valor_validacion;

        }catch (Exception e){
            return false;
        }
    }
}
