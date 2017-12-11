package com.PC_Projects.file_enc_dec.dao;

/*
 * Pradeep.kathare
 * 29-11-2017
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import org.apache.commons.io.output.StringBuilderWriter;

import com.PC_Projects.file_enc_dec.util.KeyManager;

public class File_Reader_Writer {

	public String readAndEncrypt(String fileName, String FileKey,File filepath_to_encrypt) {

		FileReader fr = null;
		BufferedReader br = null;
		StringBuilder encryptedData = new StringBuilder();

		try {
			String line = "";
			String encryptedLine = "";
			StringBuilder clearEncryptKey = new StringBuilder();
			KeyManager keyManager = new KeyManager();
			
			
			fr= new FileReader(filepath_to_encrypt+FileKey);
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {

				
				clearEncryptKey.append(line);
			}
			
			fr = new FileReader(filepath_to_encrypt+fileName);
			br = new BufferedReader(fr);

			System.out.println("filename : " + fileName);
			

			
			int i;
			while ((line = br.readLine()) != null) {

				encryptedLine = keyManager.encrypt(line, null, clearEncryptKey.toString());
				encryptedData.append(encryptedLine);
			}
			System.out.println("encryptedData : " + encryptedData);

		} catch (Exception ex) {

			System.out.println("error in readAndEncrypt   : " + ex);
			ex.printStackTrace();
		} finally {

			if (fr != null) {
				try {
					fr.close();
				} catch (Exception ex) {
					System.out.println("error while closing filereader : " + ex);
					ex.printStackTrace();
				}

			}
			if (br != null) {

				try {
					br.close();
				} catch (Exception ex) {
					System.out.println("error while closing bufferreader : " + ex);
					ex.printStackTrace();
				}

			}

		}
		return encryptedData.toString();

	}
	
	
	public void writeEncryptedDataToFile(String fileName,String encryptedData) {
		
		BufferedWriter bw = null;
		try {
			
			File file = new File("E://pradeep//"+fileName);
			
			 bw = new BufferedWriter(new FileWriter(file));
			bw.write(encryptedData);
			
		}catch(Exception ex) {
			
			System.out.println("error while closing bufferreader : " + ex);
			ex.printStackTrace();
			
		}finally {
			
			if (bw != null) {

				try {
					bw.close();
				} catch (Exception ex) {
					System.out.println("error while closing bufferreader : " + ex);
					ex.printStackTrace();
				}

			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
