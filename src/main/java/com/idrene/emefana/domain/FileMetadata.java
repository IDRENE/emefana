/**
 * 
 */
package com.idrene.emefana.domain;

import java.util.Map;
import java.util.Optional;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idrene.emefana.util.UtilityBean;
import com.mongodb.gridfs.GridFSDBFile;


/**
 * @author iddymagohe
 * @since 1.0
 *
 */

public class FileMetadata {
	private static final Logger logger = LoggerFactory.getLogger(FileMetadata.class);

	@Getter
	private String providerId;
	@Getter
	private String filetype; //PHOTO or VIDEO
	@Getter
	private boolean thumbnail;
	@Getter
	private String userId;
	@Getter
	private String fileName;
	@Getter
	private String contentType;
	@Getter 
	private String image;
	@Getter 
	private String fileDescription;
	
	public enum FILETYPE{
		PHOTO,
		VIDEO
	}
	

	public static Optional<String> fieldAsStringFromGridFSDBFile(final GridFSDBFile file,final String field) {
		return Optional.ofNullable(String.valueOf(file.getMetaData().get(field)));
	}
	
	public static Optional<String> fieldAsStringFromMap(final Map<String, String> map,final String field) {
		return Optional.ofNullable(map.get(field));
	}
	
	/**
	 * Not to be called in the application at all cost
	 */
	public FileMetadata() {}
	
	
	/**
	 * Construction of #FileMetadataas a search criteria
	 * @param providerId
	 * @param type
	 * @param userId
	 */
	public FileMetadata(String providerId, String type, String userId) {
		this.providerId = providerId;
		this.filetype = type;
		this.userId = userId;
	}
	 
	
	/**
	 * Read image from mmongoDB collection in form of #GridFSDBFile to
	 * {@link #FileMetadata} for Transfer.
	 * 
	 * @param fileData
	 */
	public FileMetadata(Optional<GridFSDBFile> fileData) {
		fileData.ifPresent(file -> {
			contentType = file.getContentType();
			fileName = file.getFilename();
			FileMetadata.fieldAsStringFromGridFSDBFile(file, MetadataFields.TYPE).ifPresent(value -> filetype = value);
			FileMetadata.fieldAsStringFromGridFSDBFile(file, MetadataFields.THUMBNAIL).ifPresent(value -> thumbnail = Boolean.valueOf(value));
			FileMetadata.fieldAsStringFromGridFSDBFile(file, MetadataFields.PROVIDER_ID).ifPresent(value -> providerId = value);
			FileMetadata.fieldAsStringFromGridFSDBFile(file, MetadataFields.USER_ID).ifPresent(value -> userId = value);
			FileMetadata.fieldAsStringFromGridFSDBFile(file, MetadataFields.FILE_DESCR).ifPresent(value -> fileDescription = value);
			try {
				UtilityBean.InputStreamToBase64(Optional.ofNullable(file.getInputStream()),file.getContentType()).ifPresent(str -> image=str);
			} catch (Exception e) {
				logger.error(e.getMessage() , e.getCause());
			}
		});
	}

	public Optional<String> oProviderId(){
		return Optional.ofNullable(providerId);
	}
	
	public Optional<String> oUserId(){
		return Optional.ofNullable(userId);
	}
	
	public Optional<String> oType(){
		return Optional.ofNullable(filetype);
	}

	
}
