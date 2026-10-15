package madstodolist.repository;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface equipoRepository extends JpaRepository<Equipo, Long> {
    public List<Equipo> findAll();
    @Query("SELECT e FROM Equipo e ORDER BY e.nombre ASC")
    public List<Equipo> findAllOrdenadoPorNombre();
    public List<Equipo> findByUsuariosId(Long usuarioId);
}