package com.krakedev.proyectos.security;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JwtUtil {
	private static final String CLAVE_SECRETA = "EstaEsUnaClaveSuperSecretaLarga1234567890";
	private static final String EMISOR = "KrakeDevBackend";
	private static final long TIEMPO_EXPIRACION = 1800000;
	
	public static String generarToken(String username, String rol) {
		
		Algorithm algorithm = Algorithm.HMAC256(CLAVE_SECRETA);
		long tiempoActual = System.currentTimeMillis();
		Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

		String tokenGenerado = JWT.create().withIssuer(EMISOR).withSubject(username)
				.withIssuedAt(new Date(tiempoActual)).withExpiresAt(fechaExpiracion).withClaim("rol", rol)
				.sign(algorithm);
		return tokenGenerado;
	
	}
	
	public static DecodedJWT validarToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(CLAVE_SECRETA);

			JWTVerifier verificador = JWT.require(algorithm).withIssuer(EMISOR).build();

			DecodedJWT tokenDecodificado = verificador.verify(token);

			return tokenDecodificado;

		} catch (Exception e) {
			System.out.println("Error al validar el token: " + e.getMessage());
			return null;
		}
	}
}
