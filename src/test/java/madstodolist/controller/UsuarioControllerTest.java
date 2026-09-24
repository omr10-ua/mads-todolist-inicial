package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testListarRegistradosSinLoginRedirigeALogin() throws Exception {
        // Intentar acceder sin sesión debe redirigir al login
        mockMvc.perform(get("/registrados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testListarRegistradosComoUsuarioNormalDaUnauthorized() throws Exception {
        // 1. Registramos un usuario normal (no admin)
        UsuarioData user = new UsuarioData();
        user.setEmail("normal@ua.es");
        user.setPassword("1234");
        user.setAdmin(false);
        UsuarioData registrado = usuarioService.registrar(user);

        // 2. Simulamos la sesión con el ID del usuario normal
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("idUsuarioLogeado", registrado.getId());

        // 3. Al intentar entrar a /registrados debe lanzar Unauthorized (401)
        mockMvc.perform(get("/registrados").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testListarRegistradosComoAdminFunciona() throws Exception {
        // 1. Registramos un administrador
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@ua.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        UsuarioData registrado = usuarioService.registrar(admin);

        // 2. Simulamos la sesión con el ID del admin
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("idUsuarioLogeado", registrado.getId());

        // 3. Debe cargar la vista "registrados" correctamente con código 2xx
        mockMvc.perform(get("/registrados").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("registrados"))
                .andExpect(content().string(containsString("Listado de Usuarios Registrados")));
    }

    @Test
    public void testCambiarBloqueoComoAdminRedirige() throws Exception {
        // 1. Registramos admin y usuario objetivo
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin2@ua.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        UsuarioData adminRegistrado = usuarioService.registrar(admin);

        UsuarioData objetivo = new UsuarioData();
        objetivo.setEmail("objetivo@ua.es");
        objetivo.setPassword("1234");
        UsuarioData objetivoRegistrado = usuarioService.registrar(objetivo);

        // 2. Sesión del admin
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("idUsuarioLogeado", adminRegistrado.getId());

        // 3. Ejecutamos la petición de bloqueo y comprobamos la redirección
        mockMvc.perform(get("/registrados/" + objetivoRegistrado.getId() + "/bloquear").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));
    }
}