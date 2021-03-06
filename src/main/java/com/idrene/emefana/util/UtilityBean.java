/**
 * 
 */
package com.idrene.emefana.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import lombok.Getter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;

import com.idrene.emefana.domain.MetadataFields;
import com.idrene.emefana.domain.FileMetadata.FILETYPE;
import com.idrene.emefana.rest.resources.types.Photo;

/**
 * @author iddymagohe
 *
 */
public class UtilityBean {

	@Getter private String SECRET_KEY;
	
	
	/**
	 * @param sECRET_KEY the sECRET_KEY to set
	 */
	public void setSECRET_KEY(String sECRET_KEY) {
		String[] secrets = sECRET_KEY.split("-");
		SECRET_KEY = secrets[secrets.length -1];
	}


	/**
	 * Used by JCE for D/Encryption
	 * @return Cipher
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException{
		return Cipher.getInstance("AES");
	}
	
	
	public static String encrypt(String plainText, SecretKey secretKey)throws Exception {
		Cipher cipher = getCipher();
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}
	
	public static String encrypt(String plainText, String encodedKey)throws Exception {
		SecretKey secretKey = secretKey(encodedKey);
		return encrypt(plainText,secretKey);
	}
	
	public static String decrypt(String encryptedText, String encodedKey)throws Exception {
		SecretKey secretKey = secretKey(encodedKey);
		return decrypt(encryptedText,secretKey);
	}

	public static String decrypt(String encryptedText, SecretKey secretKey)throws Exception {
		Cipher cipher = getCipher();
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}
	
	static SecretKey secretKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey;
	}
	
	static SecretKey secretKey(String encodedKey) throws NoSuchAlgorithmException {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		// rebuild key using SecretKeySpec
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}
	
	static String secretKeyText() throws NoSuchAlgorithmException{
		// create new key
		SecretKey secretKey = secretKey();
		// get base64 encoded version of the key
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}
   
	

	public String encodePropertyValue(String filedValue) {
		return "";
	}

	public String decodePropertyValue(String filedValue) {
		return "";
	}


	public static String generateProviderId() {
		return UUID.randomUUID().toString();
	}

	public static String generateProviderCode(String codefrom) {
		return StringUtils.upperCase(RandomStringUtils.random(5,
				codefrom.replaceAll("\\s+", "")));
	}

	public static <T> ArrayList<T> toArrayList(final Iterator<T> iterator) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator,
						Spliterator.ORDERED), false).collect(
				Collectors.toCollection(ArrayList::new));

	}

	public static <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(
				Collectors.toList());
	}

	public static Optional<String> InputStreamToBase64(Optional<InputStream> inputStream, String ext) throws IOException{
		if (inputStream.isPresent()) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			FileCopyUtils.copy(inputStream.get(), output);
			return Optional.ofNullable("data:image/"+ext+";base64," + DatatypeConverter.printBase64Binary(output.toByteArray()));
		}

		return Optional.empty();
	}
	
	public static Optional<InputStream> Base64ToInputStream(Optional<String> base64String)throws IOException {
		if (base64String.isPresent()) {
			return Optional.ofNullable(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64String.get())));
		}

		return Optional.empty();
	}
	
	
	
	
	/**
	 * To persist provider related files objects, not user profile photo
	 * @param providerId
	 * @param photo
	 * @return
	 */
	public static Map<String, String> photoMetadata(String providerId,Photo photo) {
		Map<String, String> metadataMap = new HashMap<>();
		metadataMap.put(MetadataFields.CONTENT_TYPE, photo.getFiletype());
		metadataMap.put(MetadataFields.FILE_NAME, photo.getFilename());
		metadataMap.put(MetadataFields.TYPE, FILETYPE.PHOTO.name());
		metadataMap.put(MetadataFields.FILE_DESCR, "Thumbnail image");
		metadataMap.put(MetadataFields.PROVIDER_ID, providerId);
		metadataMap.put(MetadataFields.THUMBNAIL, "true");
		
		return metadataMap;

	}
	

}
