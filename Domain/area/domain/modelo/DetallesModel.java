package area.domain.modelo;

/**
 * Modelo de los detalles de una experiencia
 * @author luis
 *
 */
public class DetallesModel {
	
	/**
	 * Id de la experiencia de la que son los detalles
	 */
	private int experienceId;
	
	/**
	 * Detalles de la experiencia en formato JSON
	 */
	private String detalles;

	public int getExperienceId() {
		return experienceId;
	}

	public void setExperienceId(int experienceId) {
		this.experienceId = experienceId;
	}

	public String getDetalles() {
		return detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

}
