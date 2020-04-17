package ca.bc.gov.educ.api.student.email.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Om
 * This class is responsible to generate JWT Token Based on claims
 */
@Component
public class JWTUtil {
  private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  /**
   * This is a shared secret key. which must be base64 encoded.
   */
  @Value("${jwt.secret.key}")
  @Setter
  private String sharedSecretKey;

  /**
   * This method will return the jwt token created Based on the parameters and private key.
   *
   * @param subject      the subject of the token
   * @param ttlInMinutes time to live in Minutes, the token will expire after the given time period.
   * @return the jwt token, generated.
   */
  public String createJWTToken(String id, String subject, Long issuedAtInMillis, Integer ttlInMinutes) {
    long currentTime = System.currentTimeMillis();
    //We will sign our JWT with our ApiKey secret
    JwtBuilder builder = Jwts.builder().setId(id).setHeaderParam("typ", "JWT")
            .setSubject(subject).claim("SCOPE", "VERIFY_EMAIL")
            .setIssuer("VerifyEmailAPI").signWith(signatureAlgorithm, this.sharedSecretKey.getBytes());


    if (issuedAtInMillis != null && issuedAtInMillis > 0) {
      builder.setIssuedAt(new Date(issuedAtInMillis));
      currentTime = issuedAtInMillis; // update the current time to use the provided one .
    } else {
      builder.setIssuedAt(new Date(currentTime));
    }
    if (ttlInMinutes != null && ttlInMinutes >= 0) {
      long expMillis = currentTime + (ttlInMinutes * 60 * 1000);
      Date exp = new Date(expMillis);
      builder.setExpiration(exp);
    }
    return builder.compact();
  }

  public Claims decodeToken(String token) {
    return Jwts.parser().setSigningKey(this.sharedSecretKey.getBytes()).parseClaimsJws(token).getBody();
  }
}
