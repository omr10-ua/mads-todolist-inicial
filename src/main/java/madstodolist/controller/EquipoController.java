package madstodolist.controller;

import madstodolist.dto.EquipoData;
import madstodolist.model.Equipo;
import madstodolist.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping("/equipos")
    public String listarEquipos(Model model) {
        // Obtenemos todos los equipos ordenados (o el método que uses para listar)
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // Añadimos la lista al modelo
        model.addAttribute("equipos", equipos);

        return "equipos";
    }

    @GetMapping("/nuevoEquipo")
    public String crearNuevoEquipo(Model model) {
        model.addAttribute("equipo", new Equipo());
        return "nuevoEquipo";
    }

    @PostMapping("/nuevoEquipo")
    public String guardarNuevoEquipo(@ModelAttribute Equipo equipo) {
        equipoService.crearEquipo(equipo.getNombre());
        return "redirect:/equipos";
    }
}
