package com.bogota.gestionUsuarios.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bogota.gestionUsuarios.modelo.Token;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long>{
    
    @Query(value = """
      select t from Token t inner join Usuario u\s
      on t.usuario.id = u.id\s
      where u.id = :id and (t.expirado = false or t.revocado = false)\s
      """)
    List<Token> findAllValidTokenByUsuario(Long id);

    Optional<Token> findByToken(String token);
}
