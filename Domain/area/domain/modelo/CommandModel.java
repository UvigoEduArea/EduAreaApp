package area.domain.modelo;


/**
 * Modelo de comando que se envía en el servidor
 * @author luis
 *
 */
public class CommandModel {
	
	/**
	 * Comando que se envía al servidor para realizar una acción
	 */
	private String command;
	
	/**
	 * Datos de la acción que se tienen que enviar al servidor
	 */
	private String data;
	
	/**
	 * Date en que se crea el comando o el record que se quiere enviar
	 */
	private String creado;
	
	private int experienceId = -1;
	
	private int lessonPlanId = -1;
	
	private int commentId = -1;
	
	/**
	 * Id del record al que se refiere la llamada si se trata de un record.
	 */
	private int recordId = -1;
	
	/**
	 * Id de la actividad a la que pertenece el record;
	 * ActivityId, ExperienceId, LessonPlanId nunca pueden ser iguales porque en el servidor se guardan en la misma tabla.
	 * Por eso se puede usar activityId para guardar cualquiera de esos datos. 
	 */
	private int activityId = 0;
	
	public CommandModel() {
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCreado() {
		return creado;
	}

	public void setCreado(String creado) {
		this.creado = creado;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

}
