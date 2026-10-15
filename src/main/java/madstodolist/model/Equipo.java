package madstodolist.model;

import javax.persistence.*;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    private String nombre;

    public Equipo(){}

    public Equipo(String equipo) {
        nombre = equipo;
    }

    public String getNombre() {
        return nombre;
    }
}
