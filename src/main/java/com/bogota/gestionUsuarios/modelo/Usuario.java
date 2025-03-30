package com.bogota.gestionUsuarios.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="USUARIOS")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nombre;
	// se puede agregar la restricción directamente en la bd, validación al recibir nuevo registro
	@Column(unique = true)
	private String correo;
	@ManyToOne
    @JoinColumn(name = "rol_id", nullable = false) // Llave foránea
    private Rol rol;
	
}
