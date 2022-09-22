package com.reborn.reborn.security.filter;

import com.reborn.reborn.security.UserPrincipal;
import com.reborn.reborn.security.jwt.AuthToken;
import com.reborn.reborn.security.jwt.TokenProvider;
import com.reborn.reborn.security.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;
    private static final String TOKEN_PREFIX = "Bearer ";
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //TODO redirect 경로 지정
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String requestUri = request.getHeader("request_uri");


        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                principal.getMember().getEmail(),
                principal.getMember().getMemberRole(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpireDate())
        );
        response.setHeader(AUTHORIZATION, TOKEN_PREFIX+accessToken.getToken());

    }


}