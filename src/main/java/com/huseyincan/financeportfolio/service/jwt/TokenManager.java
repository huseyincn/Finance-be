package com.huseyincan.financeportfolio.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Curve;
import io.jsonwebtoken.security.Jwks;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;


@Service
public class TokenManager {

    private KeyPair keyPair;

    @Value("${application.token.duration}")
    private int expireTime;

    @PostConstruct
    public void init() {
        Curve curve = Jwks.CRV.Ed25519; //or Ed448
        this.keyPair = curve.keyPair().build();
    }

    /**
     * Bu metod JWT Token'ı oluşturmaktadır.
     *
     * @param username
     * @return
     */
    public String generateToken(String username) {
        long timeMillis = System.currentTimeMillis();
        return Jwts.builder().subject(username)
                .signWith(keyPair.getPrivate(), Jwts.SIG.EdDSA)
                .issuedAt(new Date(timeMillis))
                .expiration(new Date(timeMillis + expireTime))
                .compact();
    }

    /**
     * Tokenın geçerli olup olmadığını doğrulamak için kullanılacak metod.
     * Burada doğrulama için iki şartımız olacak birincisi kullanıcı adımızın null olmaması.
     * İkincisi ise tokenın expire (süresi geçmiş) olmaması.
     *
     * @param token
     * @return
     */
    public Boolean hasTokenValid(String token) {
        if (getUserFromToken(token) != null && hasTokenExpire(token)) {
            return true;
        }
        return false;
    }

    /**
     * username'i token içerisinden almak için kullanacağımız metod.
     *
     * @param token
     * @return
     */
    public String getUserFromToken(String token) {
        Claims claims = parseToken(token); //Tokenı parse etmek için yazdığımız metodumuzu çağırıyoruz.
        return claims.getSubject(); //Kullanıcı adını claimsler arasından alıyoruz.

    }

    /**
     * Token'ın expire olup olmadığını kontrol edecek metod.
     *
     * @param token
     * @return
     */
    public boolean hasTokenExpire(String token) {
        Claims claims = parseToken(token); //Tokenı tekrardan parse edip claimlerimizi alıyoruz.
        Date now = new Date(System.currentTimeMillis()); //Bir zaman oluşturuyoruz.
        return claims.getExpiration().after(now); //expire time'ın yukarda oluştumuş olduğumuz zamandan sonra
        // olup olmadığını kontrol ediyoruz.
    }

    /**
     * Tokenı parse etmek için kullanacağımız metod.
     *
     * @param token
     * @return
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic()) // <-- Bob's Edwards Curve public key
                .build().parseSignedClaims(token).getPayload();
    }

}