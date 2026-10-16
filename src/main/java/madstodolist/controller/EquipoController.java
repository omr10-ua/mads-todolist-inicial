package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/equipos")
    public String listarEquipos(Model model) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // 1. Recuperamos la entidad Usuario (en lugar de UsuarioData)
        Usuario usuario = usuarioService.recuperarUsuario(usuarioId);
        boolean esAdmin = usuario != null && usuario.getAdmin() != null && usuario.getAdmin();

        // 2. Resto de datos habituales
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        List<EquipoData> equiposDelUsuario = equipoService.equiposUsuario(usuarioId);

        List<Long> idsEquiposDelUsuario = equiposDelUsuario.stream()
                .map(EquipoData::getId)
                .collect(Collectors.toList());

        model.addAttribute("equipos", equipos);
        model.addAttribute("idsEquiposDelUsuario", idsEquiposDelUsuario);
        model.addAttribute("esAdmin", esAdmin);

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

    @GetMapping("/equipos/{id}")
    public String verDetalleEquipo(@PathVariable Long id, Model model) {
        // 1. Recuperamos los datos del equipo
        EquipoData equipo = equipoService.recuperarEquipo(id);

        // 2. Recuperamos los usuarios que pertenecen a ese equipo (si tienes un método para ello)
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(id);

        // 3. Los añadimos al modelo
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", usuarios);

        return "equipoDetalle"; // Nombre de la vista HTML que crearemos ahora
    }

    @GetMapping("/equipos/{id}/unirse")
    public String unirseEquipo(@PathVariable Long id, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // Llamas a tu servicio para añadir al usuario al equipo
        equipoService.añadirUsuarioAEquipo(id, usuarioId);

        flash.addFlashAttribute("mensaje", "Te has unido al equipo correctamente");
        return "redirect:/equipos/" + id;
    }

    @GetMapping("/equipos/{id}/salir")
    public String salirEquipo(@PathVariable Long id) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // Método equivalente en tu servicio para eliminar al usuario del equipo
        equipoService.eliminarUsuarioDeEquipo(id, usuarioId);
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/editar")
    public String editarEquipoForm(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            EquipoData equipo = equipoService.recuperarEquipo(id);
            model.addAttribute("equipo", equipo);
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Equipo no encontrado");
            return "redirect:/equipos";
        }

        return "equipoEditar"; // Nombre de la vista HTML para editar
    }

    @PostMapping("/equipos/{id}/editar")
    public String guardarEdicionEquipo(@PathVariable Long id, @ModelAttribute Equipo equipoForm, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // El servicio comprobará si es administrador
        equipoService.modificarNombreEquipo(usuarioId, id, equipoForm.getNombre());
        flash.addFlashAttribute("mensaje", "Equipo modificado correctamente");
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/eliminar")
    public String eliminarEquipo(@PathVariable Long id, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // El servicio comprobará si es administrador
        equipoService.eliminarEquipo(usuarioId, id);
        flash.addFlashAttribute("mensaje", "Equipo eliminado correctamente");
        return "redirect:/equipos";
    }
}
