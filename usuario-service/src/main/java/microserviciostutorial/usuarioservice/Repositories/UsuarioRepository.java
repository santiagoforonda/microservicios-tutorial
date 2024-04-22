package microserviciostutorial.usuarioservice.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import microserviciostutorial.usuarioservice.Entidades.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {

    
    
}
