package com.assignment.postblog.jwt;

import com.assignment.postblog.model.MemberRoleEnum;
import com.assignment.postblog.security.MemberDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    private final MemberDetailsServiceImpl memberDetailsServiceImpl;
    @Value("${jwt.token.key}")
    private String secretKey;
    //토큰 유효시간 설정
    private Long tokenValidTime = 240 * 60 * 1000L;

    //secretkey를 미리 인코딩해줌, application.properties에 저장했던 시크릿키를 암호화로 사용하기위해 인코딩해주는 작업
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    //JWT 토큰 생성, 로그인 시도 시 호출되는 메서드
    //토큰을 만들 떄, 핵심적으로 사용자 id, 사용자 권한, 토큰 만료시간이 필요하다.
    public String createToken(String nickname, MemberRoleEnum role) {
        //payload 설정
        //registered claims
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setSubject("access_token") //토큰 제목
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime)); //토큰 만료기한

        //private claims
        claims.put("nickname", nickname); //정보는 key - value 쌍으로 저장
        claims.put("role", role);

        return Jwts.builder()
                .setHeaderParam("typ","JWT") //헤더
                .setClaims(claims) //페이로드
                .signWith(SignatureAlgorithm.ES256, secretKey) //서명, 사용할 암호화 알고리즘에 들어갈 secretKey 세팅
                .compact();
    }
    //JWT 토큰에서 인증정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailsServiceImpl.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }
    //토큰에서 회원 정보 추출, 토큰에 저장한 유저 식별값을 추출해줌 (nickname)
    public String getUserPk(String token) {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("nickname");
    }
    //Request 의 Header에서 token 값을 가져온다. "Authorization" : "Token"값
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("JWT");
    }
    //토큰의 유효성 + 만료일자 확인 -> 토큰을 만들 때 설정한 만료기간과 지금의 시간을 비교해서 만료됬다면 false 그외 exception -> false
    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
