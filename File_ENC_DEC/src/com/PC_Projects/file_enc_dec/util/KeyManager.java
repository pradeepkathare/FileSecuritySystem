package com.PC_Projects.file_enc_dec.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

public class KeyManager {
	public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
	public static final String MESSAGE_HASH_FIELD_SEPARATOR = "|";
	  public static final String SHA_256_ALGORITHM_NAME = "SHA-256";

	  public static final String SECRET_KEY_SPEC_CIPHER_ALGORITHM = "AES";
	  public static final String DEFAULT_AES_IV_STRING = "00000000000000000000000000000000";
	  private SecretKey interoperableEncryptKey;
	
	/**
	   *
	   * @param base64EncodedString
	   * @return
	   */
	  
	  private static Logger logger = Logger.getLogger(KeyManager.class);
	  public String decrypt(String base64EncodedString, String ivString) {

	      if (ivString == null || ivString.trim().length() == 0) {
	          ivString = DEFAULT_AES_IV_STRING;
	      }

	      byte[] iv = DatatypeConverter.parseHexBinary(ivString);
	      byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64EncodedString);
	      byte[] decryptedBytes = decrypt(decodedBytes, iv);
	      String decryptedString = new String(decryptedBytes);
	      return decryptedString;
	  }

	  /**
	   *
	   * @param input : plain-text
	   * @return : base64-encoded-string
	   * 
	   */
	  public void encryptKeyInit(byte[] encryptionKeyBytes){
		  
		 
	      interoperableEncryptKey = new SecretKeySpec(encryptionKeyBytes, SECRET_KEY_SPEC_CIPHER_ALGORITHM);
	      logger.debug("secret-key is : "+interoperableEncryptKey.toString().length());
	  }
	  
	  private SecretKey fetchInteroperableEncryptionSecretKey(String clearEncryptKey) {

	      //String unwrappedEncryptionKeyTemp = unwrappedEncryptionKey();
		  String unwrappedEncryptionKeyTemp = clearEncryptKey;
	      
	      byte[] encryptionKeyBytes = DatatypeConverter.parseHexBinary(unwrappedEncryptionKeyTemp);
	      if (encryptionKeyBytes != null) {
	          logger.debug( "EncKey-ByteLength :" + encryptionKeyBytes.length);
	          encryptKeyInit(encryptionKeyBytes);
	      }

	      return interoperableEncryptKey;
	  }

	  public String encrypt(String input, String ivString,String clearEncryptKey) {

		  //logger.debug("templog : clear key  : "+clearEncryptKey);
		  String encryptedString = null;
	     try{
	    	 
	    	 if (ivString == null || ivString.trim().length() == 0) {
		          ivString = DEFAULT_AES_IV_STRING;
		      }
		      SecretKey Encryptkey = fetchInteroperableEncryptionSecretKey(clearEncryptKey);
		      //logger.debug("templog : Secret key  : "+Encryptkey);
		      
		      byte[] iv = DatatypeConverter.parseHexBinary(ivString);
		      byte[] encryptedBytes = encrypt(input.getBytes(), iv);
		      encryptedString = DatatypeConverter.printBase64Binary(encryptedBytes);
		    
	     }catch(Exception ex){
	    	 logger.error("exception during encryption : "+ex,ex);
	    	// ex.printStackTrace();
	     }
	     return encryptedString;
	  }

	  public byte[] decrypt(byte[] input, byte[] iv) {
	      return handleEncryptDecrypt(input, iv, false);
	  }

	  public byte[] encrypt(byte[] input, byte[] iv) {
	      return handleEncryptDecrypt(input, iv, true);
	  }

	  private byte[] handleEncryptDecrypt(byte[] input, byte[] iv, boolean encrypt) {
	      byte[] result = null;

	      try {

	          Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
	          int mode = (encrypt) ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
	          IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	          aesCipher.init(mode,interoperableEncryptKey, ivParameterSpec);
	          result = aesCipher.doFinal(input);

	      } catch (NoSuchAlgorithmException e) {
	          logger.error( e.getMessage() + ":" + e,e);
	      } catch (NoSuchPaddingException e) {
	    	  logger.error( e.getMessage() + ":" + e,e);
	      } catch (InvalidKeyException e) {
	    	  logger.error( e.getMessage() + ":" + e,e);
	      } catch (IllegalBlockSizeException e) {
	    	  logger.error( e.getMessage() + ":" + e,e);
	      } catch (BadPaddingException e) {
	    	  logger.error( e.getMessage() + ":" + e,e);
	      } catch (InvalidAlgorithmParameterException e) {
	    	  logger.error( e.getMessage() + ":" + e,e);
	      }

	      return result;
	  }
	  
	  private String buildHashInput(List<String> hashInput, String hashKey) {

	      StringBuilder stringBuilderTemp = new StringBuilder();
	      String result = null;
	      boolean firstLoop = true;

	      if (hashInput != null) {

	          for (String string : hashInput) {

	              if (firstLoop) {
	                  stringBuilderTemp.append(string);
	                  stringBuilderTemp.append(MESSAGE_HASH_FIELD_SEPARATOR);
	              } else {
	                  stringBuilderTemp.append(MESSAGE_HASH_FIELD_SEPARATOR);
	                  stringBuilderTemp.append(string);
	              }

	          }
	      }

	      //stringBuilderTemp.append(MESSAGE_HASH_FIELD_SEPARATOR);
	      stringBuilderTemp.append(hashKey);
	      result = stringBuilderTemp.toString();
	      return result;
	  }
/*	  public boolean validateInteroperableHash(List<String> inputWithoutHashKey, String messageHashToValidate) {
	      String hashValueGenerate = generateInteroperableHash(inputWithoutHashKey);
	      boolean result = hashValueGenerate.toLowerCase().equals(messageHashToValidate.toLowerCase());
	      return result;

	  }*/

	  public String generateInteroperableHash(List<String> inputWithoutHashKey,String clearSharedSecretKey) {
	      String hashInput = buildHashInput(inputWithoutHashKey, clearSharedSecretKey);
	      String hashValue = sha256(hashInput);

	      String cardnumber = hashInput.substring(16, 32);
	      String maskedCardNUmber = cardnumber.substring(0, 6) + "xxxxxx" + cardnumber.substring(12);
	      String messageHashInput = hashInput.substring(0, 16) + maskedCardNUmber + hashInput.substring(32);
	      logger.debug("cardnumber : "
	              + cardnumber + " hashInput : " + hashInput
	              + "maskedCardNUmber : " + maskedCardNUmber + "messageHashInput : " + messageHashInput);
	      return hashValue;
	  }
	  
	  public static String sha256(String input) {
	      byte[] digestBytes = sha256(input.getBytes());
	      String base64EncodedData = encodeBase64(digestBytes);
	      return base64EncodedData;
	  }

	  public static byte[] sha256(byte[] input) {

	      byte[] result = null;
	      try {
	          MessageDigest messageDigest = MessageDigest.getInstance(SHA_256_ALGORITHM_NAME);
	          result = messageDigest.digest(input);
	      } catch (NoSuchAlgorithmException e) {
	          logger.error(e.getMessage() + ":" + e,e);
	      }

	      return result;

	  }


	  public static String encodeBase64(byte[] input) {
	      String result = DatatypeConverter.printBase64Binary(input);
	      return result;
	  }

	  public static byte[] decodeBase64(String input) {
	      byte[] result = DatatypeConverter.parseBase64Binary(input);
	      return result;
	  }
	  
}
