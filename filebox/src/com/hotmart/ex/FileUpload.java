package com.hotmart.ex;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "fileuploaded")
@ApiModel(value = "FileUpload")
public class FileUpload implements Serializable {

	/** UID */
	private static final long serialVersionUID = 2040090388674770342L;
	private String userID;
	private String fileName;
	private UploadStatus status;
	private long uploadTime;
	private int chunksCount;
	private String downloadLink;

	public FileUpload() {
	}

	public FileUpload(String userID, String fileName, String downloadLink, UploadStatus status, long uploadTime,
			int chunksCount) {
		this.userID = userID;
		this.status = status;
		this.uploadTime = uploadTime;
		this.chunksCount = chunksCount;
		this.fileName = fileName;
		this.downloadLink = downloadLink;
	}

	@ApiModelProperty(value = "Nome do arquivo.")
	public String getFileName() {
		return fileName;
	}

	@XmlElement
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@ApiModelProperty(value = "Identificador do usuário .")
	public String getUserID() {
		return userID;
	}

	@XmlElement
	public void setUserID(String userID) {
		this.userID = userID;
	}

	@ApiModelProperty(value = "Status do upload do arquivo.", allowableValues = "SUCCESS,FAIL,LOADING")
	public UploadStatus getStatus() {
		return status;
	}

	@XmlElement
	public void setStatus(UploadStatus status) {
		this.status = status;
	}

	@ApiModelProperty(value = "Tempo gasto no upload do arquivo. É atualizado a medida que o upload do arquivo é realizado.")
	public long getUploadTime() {
		return uploadTime;
	}

	@XmlElement
	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}

	@ApiModelProperty(value = "Quantidade de pedaços que o arquivo foi fracionado.")
	public int getChunksCount() {
		return chunksCount;
	}

	@XmlElement
	public void setChunksCount(int chunksCount) {
		this.chunksCount = chunksCount;
	}

	@ApiModelProperty(value = "Link para download do arquivo.")
	public String getDownloadLink() {
		return downloadLink;
	}

	@XmlElement
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public String getKey() {
		String thisKey = this.userID + "_" + this.fileName;
		return thisKey;
	}

	@Override
	public boolean equals(Object otherFile) {
		return this.getKey().equals(((FileUpload) otherFile).getKey());
	}

	@Override
	public int hashCode() {
		return this.getKey().hashCode();
	}
}
