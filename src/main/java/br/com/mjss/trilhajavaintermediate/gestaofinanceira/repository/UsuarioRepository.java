package br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
