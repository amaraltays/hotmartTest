package com.hotmart.ex;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.common.base.Strings;
import com.hotmart.ex.errorhandling.RequiredParameterException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Serviço Rest para upload de arquivos fracionados e listagem de arquivos
 * 
 * @author Tays
 *
 */
@Api(value = "FileUploadedService : Faz upload de arquivos grandes quebrando-os em pedaços e lista os arquivos enviados.")
@Path("/FileUploadedService")
public class FileUploadedService {

	@Context
	private ServletContext context;
	@Context
	private UriInfo uriInfo;

	@GET
	@Path("/filesUp")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Lista os arquivos que foram submetidos a upload.", notes = "Fornece informações durante o upload como status e tempo gasto.", response = com.hotmart.ex.FileUpload.class, responseContainer = "List")
	public List<FileUpload> listFile() {
		return FileManager.getAllFilesUpload();
	}

	@POST
	@Path("/uploadLargeFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Recebe via upload os arquivos selecionados.")
	public Response uploadLargeFile(
			@ApiParam("Quantidade de pedaços que o arquivos foi quebrado.") @FormDataParam("chunks") Integer chunks,
			@ApiParam("Índice do pedaço do arquivo enviado") @FormDataParam("chunk") Integer chunk,
			@ApiParam("Identificador do usuário") @FormDataParam("userId") String userID,
			@ApiParam("Nome do arquivo enviado.") @FormDataParam("name") String fileName,
			@ApiParam("Tempo gasto no upload do arquivo. Acumula o tempo de todos os pedações enviados até então.") @FormDataParam("uploadTime") Long uploadTime,
			@ApiParam("Arquivo para upload.") @FormDataParam("file") InputStream uploadedInputStream) {
		if (Strings.isNullOrEmpty(userID)) {
			throw new RequiredParameterException("Parametro userId é obrigatório.");
		}

		final String uploadBaseDir = "/uploads";
		String baseUri = uriInfo.getBaseUri().toString();
		baseUri = baseUri.substring(0, baseUri.lastIndexOf('/'));
		String baseDownloadUri = baseUri.replace(baseUri.substring(baseUri.lastIndexOf('/')), uploadBaseDir) + '/';
		FileManager fileManager = new FileManager(context.getRealPath(uploadBaseDir), baseDownloadUri);
		fileManager.saveFile(uploadedInputStream, fileName, userID, chunks, chunk, uploadTime);
		return Response.ok().build();
	}
}
