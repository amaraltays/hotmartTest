package com.hotmart.ex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

/**
 * Gerencia os arquivos contento métodos para salvar arquivos em pedaços.
 * 
 * @author Tays
 *
 */
public class FileManager {
	/** Diretório de upload de arquivos */
	private static final String UPLOAD_DIR = "uploads";
	/** Diretório de upload dos pedaçoes dos arquivos */
	private static final String CHUNK_DIR = "chunks";
	/** URI base para download dos arquivos. */
	private String baseDownloadUri;
	/** Diretório de trabalho usado para salvar e recuperar os arquivos. */
	private String workingDir;
	/**
	 * Mapa de arquivos inseridos. Key: chave composta pelo id do usuário com o
	 * nome do arquivo, value: arquivo correspondente armazenado.
	 */
	private static final Map<String, FileUpload> filesMap = new HashMap<>();
	/** Log dessa classe. */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FileManager.class);

	/**
	 * Construtor.
	 * 
	 * @param workingDir
	 *            diretório base para salvar os arquivos e seus pedaços
	 * @param baseDownloadUri
	 *            uri base para download do arquivo
	 */
	public FileManager(String workingDir, String baseDownloadUri) {
		this.workingDir = workingDir;
		this.baseDownloadUri = baseDownloadUri;
	}

	/**
	 * Salva o arquivo no {@code workingDir}
	 * 
	 * @param fileInputStream
	 *            arquivo/pedaço a ser salvo
	 * @param fileName
	 *            nome do arquivo
	 * @param userID
	 *            identificador do usuário salvando o arquivo
	 * @param chunks
	 *            número total de pedaços do arquivo
	 * @param chunk
	 *            índice do pedaço do arquivo a ser salvo
	 * @param uploadTime
	 *            tempo de upload do arquivo até o momento, acumula o tempo de
	 *            todos pedaços ja salvo
	 */
	public void saveFile(InputStream fileInputStream, String fileName, String userID, Integer chunks, Integer chunk,
			Long uploadTime) {
		FileUpload fileUpload = null;
		File fileSaved = null;
		try {
			if (chunks != null && chunk != null) {
				String fileChunkLocation = getFilePath(userID, CHUNK_DIR);
				fileSaved = this.saveFile(fileInputStream, fileChunkLocation, fileName, true);
				if (fileSaved != null) {
					fileUpload = new FileUpload(userID, fileName, this.getFileLink(fileSaved, userID),
							UploadStatus.LOADING, uploadTime, chunks);
					if (chunks.intValue() - 1 == chunk.intValue()) {
						String fileLocation = getFilePath(userID, UPLOAD_DIR);
						File fileDir = createDirIfNotExists(fileLocation);
						File fileDest = new File(fileDir, fileName);
						Files.move(new File(fileChunkLocation, fileName).toPath(), fileDest.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
						fileUpload = new FileUpload(userID, fileName, this.getFileLink(fileDest, userID),
								UploadStatus.SUCCESS, uploadTime, chunks);
					}
				}
			} else {
				fileSaved = this.saveFile(fileInputStream, fileName, userID, uploadTime);
				if (fileSaved != null) {
					fileUpload = new FileUpload(userID, fileName, this.getFileLink(fileSaved, userID),
							UploadStatus.SUCCESS, uploadTime, chunks);
				}
			}
		} catch (IOException | URISyntaxException e) {
			final String errorMessage = "Error saving file " + fileName;
			LOG.error(errorMessage, e);
			throw new WebApplicationException(errorMessage, e);
		} finally {
			if (fileUpload == null) {
				fileUpload = new FileUpload(userID, fileName, null, UploadStatus.FAIL, uploadTime, chunks);
			}
			filesMap.put(fileUpload.getKey(), fileUpload);
		}

	}

	/**
	 * @return todos os arquivos armazenados
	 */
	public static List<FileUpload> getAllFilesUpload() {
		ArrayList<FileUpload> allFiles = new ArrayList<FileUpload>();
		allFiles.addAll(filesMap.values());
		return allFiles;
	}

	/**
	 * @param userID
	 *            id do usuário
	 * @param downloadDir
	 *            diretório do arquivo.
	 * @return diretório base para o arquivo considerando o nome do usuário
	 */
	private String getFilePath(String userID, String downloadDir) {
		return workingDir + File.separatorChar + downloadDir + File.separatorChar + userID;
	}

	private File saveFile(InputStream fileInputStream, String fileName, String userID, long uploadTime)
			throws IOException, URISyntaxException {
		String fileLocation = getFilePath(userID, UPLOAD_DIR);
		File fileSaved = this.saveFile(fileInputStream, fileLocation, fileName, false);
		if (fileSaved != null) {
			FileUpload fileUpload = new FileUpload(userID, fileName, this.getFileLink(fileSaved, userID),
					UploadStatus.SUCCESS, uploadTime, 1);
			filesMap.put(fileUpload.getKey(), fileUpload);
			return fileSaved;
		}
		return null;
	}

	private String getFileLink(File fileSaved, String userId) throws MalformedURLException, URISyntaxException {
		URL url = new URL(this.baseDownloadUri + UPLOAD_DIR + "/" + userId + "/" + fileSaved.getName());
		URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
		return uri.toASCIIString();
	}

	private File saveFile(InputStream fileInputStream, String fileLocation, String fileName, boolean append)
			throws IOException {
		File fileDir = createDirIfNotExists(fileLocation);
		File file = new File(fileDir, fileName);
		FileOutputStream out = new FileOutputStream(file, append);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = fileInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
		return file;
	}

	private File createDirIfNotExists(String fileLocation) {
		File fileDir = new File(fileLocation);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		return fileDir;
	}
}
