package com.telcel.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.telcel.security.model.AuthenticationRequest;
import com.telcel.security.model.AuthenticationResponse;
import com.telcel.security.model.Usuario;
import com.telcel.security.repository.UsuarioRepository;
import com.telcel.security.service.UsuarioService;
import com.telcel.security.utils.JwtUtil;

@RestController
public class AuthController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@GetMapping("/dashboard")
	private String testingToken(){
		return "Bienvenido a nuestra APP " + SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	@PostMapping("/registrar")
	private ResponseEntity<?> registrarUsuario(@RequestBody AuthenticationRequest authenticationRequest){
		String nombre = authenticationRequest.getUsuario();
		String password = authenticationRequest.getPassword();
		Usuario usuario = new Usuario();
		usuario.setNombre(nombre);
		usuario.setPassword(password);
		
		try {
			usuarioRepository.save(usuario);
		} catch (Exception ex) {
			return ResponseEntity.ok(new AuthenticationResponse("Error durante el registro de usuario " + nombre));
		}
		return ResponseEntity.ok(new AuthenticationResponse("Usuario registrado " + nombre));
	}

	@PostMapping("/autenticar")
	private ResponseEntity<?> autenticarUsuario(@RequestBody AuthenticationRequest authenticationRequest){
		String nombre = authenticationRequest.getUsuario();
		String password = authenticationRequest.getPassword();
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(nombre, password));
		} catch (Exception ex) {
			return ResponseEntity.ok(new AuthenticationResponse("Error durante la autenticacion del usuario " + nombre));
		}
		
		UserDetails loadUser = usuarioService.loadUserByUsername(nombre);
		
		String generatedToken  = jwtUtil.generateToken(loadUser);
		
		return ResponseEntity.ok(new AuthenticationResponse(generatedToken));
		
	}
}
