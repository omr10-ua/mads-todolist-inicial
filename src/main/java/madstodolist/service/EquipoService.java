package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.repository.equipoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired
    private equipoRepository equipoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public EquipoData crearEquipo(String nombre) {
        Equipo equipo = equipoRepository.save(new Equipo(nombre));
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData recuperarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));

        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public List<EquipoData> findAllOrdenadoPorNombre() {
        List<Equipo> equipos = equipoRepository.findAllOrdenadoPorNombre();
        return equipos.stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void añadirUsuarioAEquipo(Long id, Long id1) {
        Equipo equipo = equipoRepository.findById(id).orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));
        Usuario usuario = usuarioRepository.findById(id1).orElseThrow(() -> new EquipoServiceException("Usuario no encontrado"));
        equipo.addUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> usuariosEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));
        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        return equipo.getUsuarios().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<EquipoData> equiposUsuario(Long usuarioId) {
        try {
            if (usuarioId == null) {
                throw new EquipoServiceException("Usuario no encontrado");
            }

            boolean usuarioExiste = usuarioRepository.existsById(usuarioId);
            if (!usuarioExiste) {
                // CAMBIO AQUÍ: Lanza EquipoServiceException en lugar de UsuarioServiceException
                throw new EquipoServiceException("Usuario no encontrado");
            }

            List<Equipo> equipos = equipoRepository.findByUsuariosId(usuarioId);

            return equipos.stream()
                    .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                    .collect(Collectors.toList());

        } catch (EquipoServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new EquipoServiceException("No se encontro el equipo");
        }
    }

    @Transactional
    public void eliminarUsuarioDeEquipo(Long equipoId, Long usuarioId) {
        // 1. Buscamos el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));

        // 2. Buscamos el usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("Usuario no encontrado"));

        // 3. Desvinculamos el usuario del equipo (dependiendo de cómo lo hayas llamado en tu entidad)
        equipo.getUsuarios().remove(usuario);
    }

    @Transactional
    public void modificarNombreEquipo(Long usuarioId, Long equipoId, String nuevoNombre) {
        // 1. Verificar que el usuario existe y es admin
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EquipoServiceException("Usuario no encontrado"));

        if (usuario.getAdmin() == null || !usuario.getAdmin()) {
            throw new EquipoServiceException("El usuario no es administrador");
        }

        // 2. Buscar el equipo y cambiar el nombre
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));

        equipo.setNombre(nuevoNombre);
    }

    @Transactional
    public void eliminarEquipo(Long usuarioId, Long equipoId) {
        // 1. Verificar que el usuario existe y es admin
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EquipoServiceException("Usuario no encontrado"));

        if (usuario.getAdmin() == null || !usuario.getAdmin()) {
            throw new EquipoServiceException("El usuario no es administrador");
        }

        // 2. Buscar el equipo y eliminarlo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado"));

        equipoRepository.delete(equipo);
    }

}
