package madstodolist.repository;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface equipoRepository extends JpaRepository<Equipo, Long> {
    public List<Equipo> findAll();
}