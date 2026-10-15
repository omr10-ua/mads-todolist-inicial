package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.model.Equipo;
import madstodolist.repository.equipoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoService {

    @Autowired
    private equipoRepository equipoRepository;

    @Autowired
    private ModelMapper modelMapper;

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
}
