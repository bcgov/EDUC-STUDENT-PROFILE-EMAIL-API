package ca.bc.gov.educ.api.student.email.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JWTUtilTest {

  private JWTUtil jwtUtil;

  @BeforeEach
  public void before() {
    jwtUtil = new JWTUtil();
    // Hard coded secret key only for unit testing purpose.
    String key = "S3lMQTBEaGpQQmVwb0FIZ3BuV2FiZUlpSndVd1B1Wnd0UmNRalIzd1hxQkE2eXZZRVc0eDdLYmtZ TkFzQlBIZWlIYVpxUXZBSG5OeXVjR0VhVmNmZzNERnBGcnNmSVJoNWU3cE5GbmdFTkZSUjl4UzR0 all4WVJpUEtJNUtpNUtzbDdIblpMbmtFbG1WbHBidERDb2o1Q1ZiWkdxMjFCVk5IUFVHUHc5Skxq bXh3Z0o0TDMxV3ZmelBkSWpDQ2pjQmlucDBOS2k3ZkVoSXBhZTdHWkt5dnZOdVU5dVNSb1pDWFdh OXlrSjdhMGFPSEZNVzl1ZkJKSUFYaW9vNXRxaVplUXFVaHY4R3l1ZXJGWVVaWGEwOTJ3RDJtS2I4 QUV0ZXl0TDFLRWM4Z1RWRm5nYW1Oc1NmRWYxUHJRRGZvdnVhQm1vTHlLY1ZhWTczVU9hTzNObkVV akhvVVhnU3hUalNOSVE1bGpEZ0ZXRW85amp0Sjk0bnBDZk5uRVJjTTkwckQyQk9tWjI3UXlHT0dX ZVBldmcxekpOalhhNVFGR0h3bTc2ZUlFN0RiaUNhUkJzblRzc2Q3YWhOTjRJN3dNREtKMmdXVzZE NHdzZDZxWmJkU3VPcEw3TGRFU1JNNVJFczZocnUzV1d0Z2RIbGRhcXlt";

    jwtUtil.setSharedSecretKey(key);
  }

  @Test
  public void testCreateJWTToken_WhenValidClaimsAreProvided_ShouldReturnAToken() throws Exception {
    String token = jwtUtil.createJWTToken("123", "test@gmail.com", 0L, 30);
    assertNotNull(token);
  }

  @Test
  public void testDecodeJWTToken_WhenValidClaimsAreProvided_ShouldReturnAToken() throws Exception {
    String token = jwtUtil.createJWTToken("123", "test@gmail.com", 0L, 1440);
    Claims claims = jwtUtil.decodeToken(token);
    assertNotNull(claims);
  }


  @Test
  public void testJWTToken_WhenExpired_ShouldThrowExpiredJwtException() throws Exception {
    long currentTimeInMillis = System.currentTimeMillis();
    String token = jwtUtil.createJWTToken("123", "test@gmail.com", (currentTimeInMillis - 120000), 1);
    assertThrows(ExpiredJwtException.class, () -> jwtUtil.decodeToken(token));
  }


}
