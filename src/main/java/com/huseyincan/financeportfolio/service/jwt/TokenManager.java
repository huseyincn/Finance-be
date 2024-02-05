package com.huseyincan.financeportfolio.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.Jwts.SIG.HS512;

@Service
public class TokenManager {


    private SecretKey key;

    @Value("${application.token.duration}")
    private int expireTime;

    public TokenManager() {
        this.key = HS512.key().build();
    }

    /**
     * Bu metod JWT Token'ı oluşturmaktadır.
     *
     * @param username
     * @return
     */
    public String generateToken(String username) {
        long timeMillis = System.currentTimeMillis();
        /*pom.xml'de dahil etmiş olduğumuz JWT token bağımlılığını kullanarak tokenımızı üretiyoruz.
        Burada token'ı kütüphane kullanmadan kendi metodlarımızı yazarak da oluşturabilirdik.
        Fakat daha sonrasında muhtemelen bakım, dökümantasyon ve okunabilirlik konularında güçlük yaşayacaktık.*/

        return Jwts.builder() //Jwts factory tasarım kalıbını implemente eden bir sınıf. Factory tasarım kalıbı
                //ile ilgili olarak https://github.com/yusufyilmazfr/tasarim-desenleri-turkce-kaynak/tree/master/factory/java
                // adresinden örnek kodlara ulaşabilirsiniz.
                .subject(username)//Tokenın payloadında bulunacak subject (bu token kim için oluşturuldu) alanın setlenmesi.
                .issuer("FinanceMobile")
                .issuedAt(new Date(timeMillis))
                .expiration(new Date(timeMillis + expireTime))
                .signWith(key,HS512)
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
                .verifyWith(key)
                .build()//Kullanıcıdan aldığımız tokenı parse etmek için secret keyimizi setliyoruz.
                .parseSignedClaims(token)
                .getPayload();
    }

}