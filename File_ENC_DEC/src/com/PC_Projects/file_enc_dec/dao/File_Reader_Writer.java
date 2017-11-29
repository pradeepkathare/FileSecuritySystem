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

	public void readAndEncrypt(String fileName, String clearEncryptKey) {

		FileReader fr = null;
		BufferedReader br = null;

		try {
			String line = "";
			String encryptedLine = "";
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			System.out.println("filename : " + fileName);
			KeyManager keyManager = new KeyManager();
			StringBuilder encryptedData = new StringBuilder();

			System.out.println("encryptedData : " + encryptedData);
			int i;
			while ((line = br.readLine()) != null) {

				encryptedLine = keyManager.encrypt(line, null, clearEncryptKey);
				encryptedData.append(encryptedLine);
			}

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
