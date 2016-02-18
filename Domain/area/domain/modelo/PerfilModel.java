package area.domain.modelo;

/**
 * Modelo de datos del perfil del usuario
 * @author luis
 *
 */
public class PerfilModel {
	
	
	public static int TEACHER 	= 0;
	public static int STUDENT 	= 1;
	public static int ADMIN		= 2;
	
	

	/**
	 * Email del usuario
	 */
	private String email;
	/**
	 * Password del usuario guardada como MD5
	 */
	private String pass;
	/**
	 * Id del usuario en el servidor
	 */
	private int id;
	/**
	 * Tipo de usuario
	 */
	private int type;
	
	
	public PerfilModel(String email, String pass) {
		this.email = email;
		this.pass = pass;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setType(String type){
		if(type.equals("Student"))
			setType(STUDENT);
		if(type.equals("Teacher"))
			setType(TEACHER);
		if(type.equals("Admin"))
			setType(ADMIN);
	}
	
	
	
}
