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

	public String readAndEncrypt(String fileNameEncrypt, String keyFileName,String tempDirPath) {

		FileReader fr = null;
		BufferedReader br = null;
		StringBuilder encryptedData = new StringBuilder();

		try {
			String line = "";
			String encryptedLine = "";
			StringBuilder clearEncryptKey = new StringBuilder();
			KeyManager keyManager = new KeyManager();
			
			//File filepath = new File("");
			fr= new FileReader(tempDirPath+keyFileName);
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {

				
				clearEncryptKey.append(line);
			}
			
			fr = new FileReader(tempDirPath+fileNameEncrypt);
			br = new BufferedReader(fr);

			System.out.println("filename : ");
			

			
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
	
	
	public String writeEncryptedDataToFile(String fileName,String encryptedData,String filePathToEncrypt) {
		
		BufferedWriter bw = null;
		String res = "sucess";
		try {
			
			File file = new File(filePathToEncrypt+fileName);
			
			 bw = new BufferedWriter(new FileWriter(file));
			bw.write(encryptedData);
			
		}catch(Exception ex) {
			
			System.out.println("error while closing bufferreader : " + ex);
			ex.printStackTrace();
			res = "failure";
			
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
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
