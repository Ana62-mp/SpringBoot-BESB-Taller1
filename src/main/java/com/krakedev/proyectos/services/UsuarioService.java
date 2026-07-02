package com.krakedev.proyectos.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(Usuario usuario) {
        String passwordEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

        usuario.setPassword(passwordEncriptada);

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String username, String password) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioEncontrado = usuarioOptional.get();

            boolean passwordCorrecta = BCrypt.checkpw(password, usuarioEncontrado.getPassword());

            if (passwordCorrecta) {
                return usuarioEncontrado;
            }
        }

        return null;
    }
    
 
}