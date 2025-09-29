package com.gondo.map.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gondo.map.domain.common.CommResponse;
import com.google.common.base.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 인증이 필요 없는 경로들 (HTTP 메서드 상관없이)
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout",    // 로그아웃은 퍼블릭 (토큰 있으면 처리, 없으면 그냥 통과)
            "/api/auth/forgotPw",   // 비번 잊음
            "/api/auth/resetPw",   // 비번 재설정

            "/api/user/signUp",
            "/api/user/checkId",
            "/api/user/checkNick",
            "/api/user/reqCd",      // 인증코드 전송요청
            "/api/user/verifyCd",   // 인증코드 검증

            "/api/user/findId",   // 아이디 찾기
            "/api/user/findPw",   // 비번 찾기

            // GET - 목록 조회
            "/api/site/items",
            "/api/hist/items",
            "/api/hist/year-items",
            "/api/category/items",
            "/api/category/stat",

            // 이미지 조회
            "/images/**"
    );
    // 반드시 인증이 필요한 경로들 (HTTP 메서드별로)
    private static final Map<String, List<String>> AUTH_REQUIRED_PATHS = Map.of(
            "POST", Arrays.asList(
                    "/api/hist/item",      // 등록
                    "/api/site/item",
                    "/api/category/item",
                    "/api/user/recovery-mail",
                    "/api/user/verify-mail"
            ),
            "PATCH", Arrays.asList(
                    "/api/hist/item",      // 수정
                    "/api/site/item",
                    "/api/category/item",
                    "/api/user/nick",       // 닉네임 변경
                    "/api/auth/change-password"       // 비밀번호 변경
            ),
            "DELETE", Arrays.asList(
                    "/api/hist/item",      // 삭제
                    "/api/site/item",
                    "/api/category/item"
            ),
            "GET", Arrays.asList(
                    "/api/auth/me"         // 내 정보 조회
            )
    );

    private boolean isAuthRequiredPath(String uri, String method) {
        List<String> pathsForMethod = AUTH_REQUIRED_PATHS.get(method);
        if (pathsForMethod == null) return false;

        return pathsForMethod.stream().anyMatch(uri::contains);
    }

    private boolean isPublicPath(String uri) {
        return PUBLIC_PATHS.stream().anyMatch(uri::contains);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 정적 리소스(이미지)는 JWT 검증 없이 즉시 통과
        if (requestURI.startsWith("/map/images/")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // header 에서 Jwt 추출
        String role = this.getCookieValue(request, "role");
        String token = jwtTokenProvider.resolveToken(request);

        if (!Strings.isNullOrEmpty(role) && role.equals("VIEWER")) {
            if (isAuthRequiredPath(requestURI, method)) {
                sendErrorResponse(response, "AUTH_REQUIRED", "로그인이 필요한 서비스입니다", HttpStatus.UNAUTHORIZED);
                return;
            }

            // 비회원은 토큰 검증 없이 통과
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 퍼블릭 경로는 토큰 검증 없이 통과
        if (isPublicPath(requestURI)) {
            // 로그아웃의 경우 토큰이 있으면 검증해서 유효한 로그아웃인지 확인
            if (requestURI.contains("/api/auth/logout") && token != null) {
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    // 토큰이 무효해도 로그아웃은 허용 (쿠키 삭제만 함)
                } catch (Exception e) {
                    // 로그아웃은 에러가 나도 통과
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            // 유효 토큰 확인
            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰 유효시 토큰으로부터 유저 정보 받아옴
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    // Security Context 에 유저 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    handleTokenError(request, response, token);
                    return;
                }
            } else {
                // 토큰이 없는경우
                if (isAuthRequiredPath(requestURI, method)) {
                    sendErrorResponse(response, "AUTH_REQUIRED", "로그인이 필요한 서비스입니다", HttpStatus.UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            if (isAuthRequiredPath(requestURI, method)) {
                sendErrorResponse(response, "JWT_ERROR", "인증에 실패했습니다", HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void handleTokenError(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            sendErrorResponse(response, "JWT_EXPIRED", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED);
        } else {
            sendErrorResponse(response, "JWT_INVALID", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED);
        }
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }


    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommResponse<String> responseBody = new CommResponse<>();
        responseBody.setCode(status.value());
        responseBody.setMessage(message);
        responseBody.setData(errorCode);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
