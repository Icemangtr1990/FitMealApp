package es.dsw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Crea un esquema pequeño y datos de prueba en la base de datos H2 en memoria.
 * Se usa antes de cada test para que cada prueba empiece desde cero.
 */
public final class TestDatabase {

	private static final String URL = "jdbc:h2:mem:fitmeal;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

	private TestDatabase() {
	}

	public static void reset() throws Exception {
		try (Connection connection = DriverManager.getConnection(URL, "sa", "");
			 Statement statement = connection.createStatement()) {

			statement.execute("DROP ALL OBJECTS");

			statement.execute("CREATE TABLE usuarios ("
					+ "id_usuario INT AUTO_INCREMENT PRIMARY KEY, "
					+ "nombre VARCHAR(100) NOT NULL, "
					+ "email VARCHAR(150) NOT NULL UNIQUE, "
					+ "password VARCHAR(255) NOT NULL, "
					+ "rol VARCHAR(20) DEFAULT 'usuario')");

			statement.execute("CREATE TABLE recetas ("
					+ "id_receta INT AUTO_INCREMENT PRIMARY KEY, "
					+ "nombre_receta VARCHAR(255) NOT NULL, "
					+ "descripcion TEXT, "
					+ "instrucciones TEXT, "
					+ "ingredientes TEXT NOT NULL, "
					+ "imagen VARCHAR(255), "
					+ "id_usuario INT NOT NULL, "
					+ "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE)");

			statement.execute("CREATE TABLE planificador ("
					+ "id_planificador INT AUTO_INCREMENT PRIMARY KEY, "
					+ "id_usuario INT NOT NULL, "
					+ "fecha DATE NOT NULL, "
					+ "id_receta INT NOT NULL, "
					+ "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE, "
					+ "FOREIGN KEY (id_receta) REFERENCES recetas(id_receta) ON DELETE CASCADE)");

			statement.execute("INSERT INTO usuarios (nombre, email, password, rol) VALUES "
					+ "('admin', 'admin@test.es', 'hash_admin', 'admin'), "
					+ "('pepe', 'pepe@test.es', 'hash_pepe', 'usuario')");

			statement.execute("INSERT INTO recetas (nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario) VALUES "
					+ "('Ensalada Cesar', 'desc', 'inst', "
					+ "'[{\"cantidad\":\"50g\",\"ingrediente\":\"queso parmesano\"},"
					+ "{\"cantidad\":\"2 unidades\",\"ingrediente\":\"huevos\"}]', 'a.jpg', 1), "
					+ "('Tortilla', 'desc', 'inst', "
					+ "'[{\"cantidad\":\"50g\",\"ingrediente\":\"queso parmesano\"},"
					+ "{\"cantidad\":\"3 unidades\",\"ingrediente\":\"huevos\"},"
					+ "{\"cantidad\":\"100ml\",\"ingrediente\":\"aceite de oliva\"}]', 'b.jpg', 1)");

			statement.execute("INSERT INTO planificador (id_usuario, fecha, id_receta) VALUES "
					+ "(1, '2026-11-10', 1), (1, '2026-11-20', 2)");
		}
	}
}
