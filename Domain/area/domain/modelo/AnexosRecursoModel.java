package area.domain.modelo;

/**
 * Modelo de datos para los anexos de los recursos
 * @author luis
 *
 */
public class AnexosRecursoModel {
	
	private int id;
	private String type;
	private String snippetUrl;
	private String document;
	private int recursoId;
	private String fileName;
	private String direccion;
	private double latitude;
	private double longitude;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getSnippetUrl() {
		return snippetUrl;
	}
	public void setSnippetUrl(String snippetUrl) {
		this.snippetUrl = snippetUrl;
	}
	public int getRecursoId() {
		return recursoId;
	}
	public void setRecursoId(int recursoId) {
		this.recursoId = recursoId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
