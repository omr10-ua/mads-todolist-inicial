package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UnauthorizedException;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession; // <-- Asegúrate de tenerlo inyectado

    @GetMapping("/registrados")
    public String listarRegistrados(Model model) {
// 1. Comprobamos quién está logueado
        Long idLogeado = managerUserSession.usuarioLogeado();
        if (idLogeado == null) {
            return "redirect:/login"; // Si no está logueado, al login
        }

        UsuarioData usuarioLogeado = usuarioService.findById(idLogeado);

        // 2. Validamos si es administrador
        if (usuarioLogeado == null || !Boolean.TRUE.equals(usuarioLogeado.getAdmin())) {
            throw new UnauthorizedException("No tienes suficientes permisos para acceder a esta página.");
        }

        // Si es admin, cargamos la vista con normalidad
        model.addAttribute("usuario", usuarioLogeado);
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);

        return "registrados";
    }

    @GetMapping("/registrados/{id}")
    public String verDetalleUsuario(@PathVariable Long id, Model model) {
        Long idLogeado = managerUserSession.usuarioLogeado();
        if (idLogeado == null) {
            return "redirect:/login";
        }

        UsuarioData usuarioLogeado = usuarioService.findById(idLogeado);

        // 2. Validamos si es administrador
        if (usuarioLogeado == null || !Boolean.TRUE.equals(usuarioLogeado.getAdmin())) {
            throw new UnauthorizedException("No tienes suficientes permisos para ver los detalles de este usuario.");
        }

        // Si es admin, cargamos los datos del usuario solicitado
        model.addAttribute("usuario", usuarioLogeado);
        UsuarioData usuarioDetalle = usuarioService.findById(id);
        model.addAttribute("usuarioDetalle", usuarioDetalle);

        return "usuarioDetalle";
    }

    @GetMapping("/registrados/{id}/bloquear")
    public String cambiarBloqueo(@PathVariable Long id) {
        // 1. Protección de seguridad: comprobar que quien hace la petición es admin
        Long idLogeado = managerUserSession.usuarioLogeado();
        if (idLogeado == null) {
            return "redirect:/login";
        }
        UsuarioData usuarioLogeado = usuarioService.findById(idLogeado);
        if (usuarioLogeado == null || !Boolean.TRUE.equals(usuarioLogeado.getAdmin())) {
            throw new UnauthorizedException("No tienes permisos suficientes.");
        }

        // 2. Ejecutamos el cambio de bloqueo
        usuarioService.cambiarBloqueo(id);

        // 3. Redirigimos de vuelta al listado
        return "redirect:/registrados";
    }
}
