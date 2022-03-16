package com.telcel.security.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.telcel.security.utils.JwtUtil;

@Component
public class JwtFiltro extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UsuarioService usuarioService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String autorizacion = request.getHeader("Autorizacion");
		String usuario = null;
		String jwtToken = null;
		
		if (autorizacion != null && autorizacion.startsWith("Portador ")) {
			jwtToken = autorizacion.substring(7);
			usuario = jwtUtil.extractUsername(jwtToken);
		}
		
		if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails detallesUsuario =  usuarioService.loadUserByUsername(usuario);
			Boolean tokenValidado = jwtUtil.validateToken(jwtToken, detallesUsuario);
			
			if (tokenValidado) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(detallesUsuario, null, detallesUsuario.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);
		
	}

}
