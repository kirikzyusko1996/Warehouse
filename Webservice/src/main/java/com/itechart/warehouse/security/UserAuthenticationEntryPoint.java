package com.itechart.warehouse.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Custom authentication entry point for user.
 */
public class UserAuthenticationEntryPoint extends DigestAuthenticationEntryPoint {

    private CorsConfiguration corsConfiguration;
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ORIGIN = "Origin";


    public CorsConfiguration getCorsConfiguration() {
        return corsConfiguration;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
//        if (isPreflight(request)) {
        if (!CollectionUtils.isEmpty(corsConfiguration.getAllowedOrigins())) {
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, corsConfiguration.checkOrigin(request.getHeader(ORIGIN)));
        }
        if (!CollectionUtils.isEmpty(corsConfiguration.getAllowedMethods())) {
            List<HttpMethod> methods = corsConfiguration.checkHttpMethod(HttpMethod.resolve(request.getMethod()));
            response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, stringify(methods));
        }
        if (corsConfiguration.getMaxAge() != null) {
            response.addHeader(ACCESS_CONTROL_MAX_AGE, corsConfiguration.getMaxAge().toString());
        } else
            response.addHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(1800));
        if (corsConfiguration.getAllowCredentials()) {
            response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(true));
        }
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        // compute a nonce (do not use remote IP address due to proxy farms)
        // format of nonce is:
        // base64(expirationTime + ":" + md5Hex(expirationTime + ":" + key))
        long expiryTime = System.currentTimeMillis() + (getNonceValiditySeconds() * 1000);
        String signatureValue = DigestUtils.md5Hex(expiryTime + ":" + getKey());
//        String signatureValue = DigestUtils.md5DigestAsHex((expiryTime + ":" + getKey()).getBytes());
//        String signatureValue = DigestAuthUtils.md5Hex(expiryTime + ":" + getKey());
        String nonceValue = expiryTime + ":" + signatureValue;
        String nonceValueBase64 = new String(Base64.encode(nonceValue.getBytes()));


        String authenticateHeader = "Digest realm=\"" + getRealmName() + "\", "
                + "qop=\"auth\", nonce=\"" + nonceValueBase64 + "\"";

        if (authException instanceof NonceExpiredException) {
            authenticateHeader = authenticateHeader + ", stale=\"true\"";
        }


        response.addHeader("WWW-Authenticate", authenticateHeader);
        response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "WWW-Authenticate");
        if (isPreflight(request))
            response.setStatus(HttpServletResponse.SC_OK);
        else
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    authException.getMessage());
//        } else
//            super.commence(request, response, authException);
    }


    private boolean isPreflight(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod());
    }

    private String stringify(List<HttpMethod> methods) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (HttpMethod method : methods) {
            if (!isFirst) {
                sb.append(", ");
            }
            sb.append(method.toString());
            isFirst = false;
        }
        return sb.toString();
    }

}
