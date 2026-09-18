package madstodolist.controller;

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
    private UsuarioService usuarioService; // O el nombre de tu servicio de usuarios

    @GetMapping("/registrados")
    public String listarRegistrados(Model model) {
        // Obtenemos la lista de usuarios desde el servicio
        List<Usuario> usuarios = usuarioService.findAll();

        // Los añadimos al modelo para que Thymeleaf pueda leerlos
        model.addAttribute("usuarios", usuarios);

        // Retorna el nombre de la vista HTML (registrados.html)
        return "registrados";
    }

    @GetMapping("/registrados/{id}")
    public String verDetalleUsuario(@PathVariable Long id, Model model) {
        // Buscamos el usuario por su ID
        UsuarioData usuario = usuarioService.findById(id);

        // Lo pasamos al modelo para que la vista pueda leer sus datos
        model.addAttribute("usuario", usuario);

        // Retorna la vista HTML de detalle
        return "usuarioDetalle";
    }
}
