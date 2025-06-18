package com.vacationstay.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // You can add custom logic here, such as storing tokens in session
        HttpSession session = request.getSession();
        session.setAttribute("user_authenticated_via_oauth2", true);
        
        // Get the intended URL from the session if it exists
        String targetUrl = (String) session.getAttribute("REDIRECT_URI");
        if (targetUrl != null) {
            session.removeAttribute("REDIRECT_URI");
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}