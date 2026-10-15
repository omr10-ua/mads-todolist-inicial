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

    public void save(Equipo equipo) {
        Equipo equipoSaved = equipoRepository.save(equipo);
        modelMapper.map(equipoSaved, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData findById(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) {
            return null;
        }
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional
    public EquipoData crearEquipo(String nombre) {
        Equipo equipo = equipoRepository.save(new Equipo(nombre));
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData recuperarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);

        if (equipo == null) {
            return null;
        }

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
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        Usuario usuario = usuarioRepository.findById(id1).orElse(null);
        equipo.addUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> usuariosEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        return equipo.getUsuarios().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());

    }
}
