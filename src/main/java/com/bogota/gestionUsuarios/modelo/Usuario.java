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
@Data
@Builder
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false, unique = true)
	private String correo;
	@ManyToOne
    @JoinColumn(name = "rol_id") // Llave foránea
    private Rol rol;
	@Column(nullable = false)
	private String clave;
	
}
