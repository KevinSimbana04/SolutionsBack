package com.int_solutions.sys.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // Almacena los tokens cerrados en /logout
    private final Set<String> tokensRevocados = ConcurrentHashMap.newKeySet();

    public void revocarToken(String token) {
        tokensRevocados.add(token);
    }

    public boolean estaRevocado(String token) {
        return tokensRevocados.contains(token);
    }
}