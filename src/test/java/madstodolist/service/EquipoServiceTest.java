package madstodolist.service;

import madstodolist.dto.UsuarioData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import madstodolist.dto.EquipoData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void crearRecuperarEquipo() {
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThat(equipo.getId()).isNotNull();

        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
    }

    @Test
    public void listadoEquiposOrdenAlfabetico() {
        // GIVEN
        // Dos equipos en la base de datos
        equipoService.crearEquipo("Proyecto BBB");
        equipoService.crearEquipo("Proyecto AAA");

        // WHEN
        // Recuperamos los equipos
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // THEN
        // Los equipos están ordenados por nombre
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto AAA");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto BBB");
    }

    @Test
    public void añadirUsuarioAEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
    }

    @Test
    public void recuperarEquiposDeUsuario() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto 2");
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        // Recuperamos los equipos del usuario
        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        // El usuario pertenece a los dos equipos
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void comprobarExcepciones() {
        // Comprobamos las excepciones lanzadas por los métodos
        // recuperarEquipo, añadirUsuarioAEquipo, usuariosEquipo y equiposUsuario
        assertThatThrownBy(() -> equipoService.recuperarEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(1L, 1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.usuariosEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.equiposUsuario(1L))
                .isInstanceOf(EquipoServiceException.class);

        // Creamos un equipo pero no un usuario y comprobamos que también se lanza una excepción
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), 1L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testModificarNombreEquipo() {
        // Usuario 1 (admin) modifica el equipo con ID 1
        equipoService.modificarNombreEquipo(1L, 1L, "Nuevo Nombre Empresa");

        EquipoData equipo = equipoService.recuperarEquipo(1L);
        assertThat(equipo.getNombre()).isEqualTo("Nuevo Nombre Empresa");
    }

    @Test
    public void testModificarNombreEquipoNoEncontrado() {
        // Usuario 1 (admin) intenta modificar un equipo que no existe (999L)
        assertThatThrownBy(() -> equipoService.modificarNombreEquipo(1L, 999L, "Nombre"))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testEliminarEquipo() {
        // Suponemos que el usuario con ID 1 es administrador y el equipo con ID 2 existe
        equipoService.eliminarEquipo(1L, 2L);

        // Como tu método recuperarEquipo lanza una excepción cuando no lo encuentra (orElseThrow):
        assertThatThrownBy(() -> equipoService.recuperarEquipo(2L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testEliminarEquipoNoEncontrado() {
        // Si intentamos borrar un equipo que no existe (ej. ID 999L) con un admin válido (ID 1L)
        assertThatThrownBy(() -> equipoService.eliminarEquipo(1L, 999L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testModificarNombreEquipoNoAdmin() {
        // Suponiendo que el usuario con ID 2 es un usuario normal (admin = false)
        assertThatThrownBy(() -> equipoService.modificarNombreEquipo(2L, 1L, "Nuevo Nombre"))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testEliminarEquipoNoAdmin() {
        // Usuario normal intentando eliminar un equipo
        assertThatThrownBy(() -> equipoService.eliminarEquipo(2L, 1L))
                .isInstanceOf(EquipoServiceException.class);
    }
}
