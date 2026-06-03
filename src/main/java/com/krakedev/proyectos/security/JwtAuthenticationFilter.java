package com.krakedev.proyectos.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.services.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component //decirle a spring que es una clase especial, y crear una instancia para que sea utilizada
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final TokenBlacklistService blackListService;
	
	public JwtAuthenticationFilter(TokenBlacklistService blackListService) {
		super();
		this.blackListService = blackListService;
	}


	@Override //peticion http (info entrante, body, header) | envia devuelta al cliente response | cadena de filtros para ver si puede pasar | Excepciones
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String authHeader = request.getHeader("Authorization");
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			
		}
		
		String token = authHeader.substring(7);
		
		if(blackListService.estaInvalidado(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Acceso denegado: Sesion cerrada");
			return;
		}
		DecodedJWT datosToken = JwtUtil.validarToken(token);
		if (datosToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Acceso Denegado: Token invalido o expirado");
			return;
		}
		
		String username = datosToken.getSubject();
		String rolOriginal = datosToken.getClaim("rol").asString();
		
		//así obliga spring security
		String rolSpring = "ROLE_"+rolOriginal;
		
		
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);
		
		//para que spring deje pasar, toda la info unida:
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
	
}
	
