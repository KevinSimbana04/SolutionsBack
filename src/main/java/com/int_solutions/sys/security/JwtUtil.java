package com.int_solutions.sys.security;

import com.int_solutions.sys.entity.auth.RoleE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JwtUtil {
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration.administrador:54000000}")
    private long expAdmin;

    @Value("${jwt.expiration.administrativo:25200000}")
    private long expAdminis;

    @Value("${jwt.expiration.tecnico:1800000}")
    private long expTecnico;

    @Value("${jwt.expiration.cliente:1800000}")
    private long expCliente;

    private SecretKey getSigningKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Calculo de tiempo de expiracion
    public long obtenerTiempoExpiracion(RoleE rol){
        if(rol== null) return expCliente;
        return switch(rol){
            case ADMINISTRADOR -> expAdmin;
            case ADMINISTRATIVO ->  expAdminis;
            case TECNICO -> expTecnico;
            case CLIENTE -> expCliente;
        };
    }

    public  String generarToken(UserDetails userDetails , RoleE rolPrincipal){
        Map<String, Object> claims = new HashMap<>();

        //Adjuntar rol y permisos
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("authorities", authorities);
        claims.put("rol", rolPrincipal != null ? rolPrincipal.name(): "ClIENTE");

        long tiempoExpiracion= obtenerTiempoExpiracion(rolPrincipal);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tiempoExpiracion))
                .signWith(getSigningKey())
                .compact();

    }

    public String extractUsername(String token){
        return  extractAllClaims(token).getSubject();
    }

    public boolean validateToken( String token , UserDetails userDetails){
        try{
            final String username= extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }catch (ExpiredJwtException e) {
            return false;
        }
    }
    // Comprobar si el token ya expiró
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Claims extractAllClaims(String token){
        return  Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
