package area.domain.modelo;

import java.util.Date;


/**
 * Modelo de los records que puede tener una experiencia
 * @author luis
 *
 */
public class RecordModel{
	
	
	/**
	 * 
	 */
	public static final int RECORD_IMAGE 				= 0;
	public static final int RECORD_VIDEO 				= 1;
	public static final int RECORD_DOCUMENT 			= 2;
	public static final int RECORD_SNIPPET 				= 3;
	public static final int RECORD_POSITIVE_COMMENT		= 4;
	public static final int RECORD_NEGATIVE_COMMENT 	= 5;
	public static final int RECORD_TEXT 				= 6;
	public static final int RECORD_IDEA 				= 7;
	
	/**
	 * Id del record
	 */
	private int id;
	
	/**
	 * Tipo de record
	 */
	private int recordTypeId;
	
	/**
	 * Nombre del tipo de record
	 */
	private String recordType;
	
	/**
	 * Título del record
	 */
	private String title;
	
	/**
	 * Descripción del record
	 */
	private String description;
	
	/**
	 * Fecha de creación del record
	 */
	private Date creationDate;
	
	/**
	 * Fecha de modificación del record
	 */
	private Date updateDate;
	
	/**
	 * Datos del record; 
	 * En caso de una imagen contiene la ruta a la imagen. Si en snippet tiene el string que hay que insertar
	 */
	private String data;
	
	/**
	 * Datos del record; 
	 * Imagen post procesada del record
	 */
	private String blurred = null;
	
	/**
	 * Datos del record; 
	 * En caso de una imagen contiene la ruta a la imagen. Si en snippet tiene el string que hay que insertar
	 */
	private String faces_array = null;
	
	
	/**
	 * Datos del record en local.
	 * La ruta al video guardado en el almacenamiento local.
	 */
	private String localData = null;
	
	/**
	 * Indica si el record está sincronizado con el servidor
	 */
	private boolean sinc = true;
	
	/**
	 * Ruta a un frame del video
	 */
	private String video_frame = null;
	
	private boolean separador = false;
	
	private String autor = "";
	
	private String permiso = "";
	
	public RecordModel() {
	}



	public int getRecordTypeId() {
		return recordTypeId;
	}



	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
	}



	public String getRecordType() {
		return recordType;
	}



	public void setRecordType(String recordType) {
		this.recordType = recordType;
		if(recordType == null) return;
		if(recordType.equals("Image"))
			setRecordTypeId(RECORD_IMAGE);
		if(recordType.equals("Video"))
			setRecordTypeId(RECORD_VIDEO);
		if(recordType.equals("Document"))
			setRecordTypeId(RECORD_DOCUMENT);
		if(recordType.equals("Snippet"))
			setRecordTypeId(RECORD_SNIPPET);
		if(recordType.equals("PositiveComment"))
			setRecordTypeId(RECORD_POSITIVE_COMMENT);
		if(recordType.equals("NegativeComment"))
			setRecordTypeId(RECORD_NEGATIVE_COMMENT);
		if(recordType.equals("FreeText"))
			setRecordTypeId(RECORD_TEXT);
		if(recordType.equals("Idea"))
			setRecordTypeId(RECORD_IDEA);
	}
	
	public void setRecordType(int recordTypeId){
		switch (recordTypeId) {
		case RecordModel.RECORD_IMAGE:
			this.recordType = "Image";
			break;
		case RecordModel.RECORD_VIDEO:
			this.recordType = "Video";
			break;
		case RecordModel.RECORD_DOCUMENT:
			this.recordType = "Document";
			break;
		case RecordModel.RECORD_SNIPPET:
			this.recordType = "Snippet";
			break;
		case RecordModel.RECORD_NEGATIVE_COMMENT:
			this.recordType = "NegativeComment";
			break;
		case RecordModel.RECORD_POSITIVE_COMMENT:
			this.recordType = "PositiveComment";
			break;
		case RecordModel.RECORD_TEXT:
			this.recordType = "FreeText";
			break;
		case RecordModel.RECORD_IDEA:
			this.recordType = "Idea";
			break;


		}
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public Date getCreationDate() {
		return creationDate;
	}



	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}



	public Date getUpdateDate() {
		return updateDate;
	}



	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}



	public String getData() {
		return data;
	}



	public void setData(String data) {
		this.data = data;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getLocalData() {
		return localData;
	}



	public void setLocalData(String localData) {
		this.localData = localData;
	}
	
	public String getBlurredImage(){
		return blurred;
	}
	
	public void setBlurredImage(String blurred_image){
		this.blurred = blurred_image;
	}
	
	public String getFacesArray(){
		return faces_array;
	}
	
	public void setFacesArray(String faces){
		this.faces_array = faces;
	}



	public String getVideoFrame() {
		return video_frame;
	}



	public void setVideoFrame(String video_frame) {
		this.video_frame = video_frame;
	}



	public boolean isSeparador() {
		return separador;
	}



	public void setSeparador(boolean separador) {
		this.separador = separador;
	}



	public String getAutor() {
		return autor;
	}



	public void setAutor(String autor) {
		this.autor = autor;
	}



	public String getPermiso() {
		return permiso;
	}



	public void setPermiso(String permiso) {
		this.permiso = permiso;
	}
}
