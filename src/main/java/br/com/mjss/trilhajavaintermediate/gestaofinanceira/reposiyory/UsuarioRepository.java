package br.com.mjss.trilhajavaintermediate.gestaofinanceira.reposiyory;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
