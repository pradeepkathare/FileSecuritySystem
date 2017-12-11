package com.PC_Projects.file_enc_dec.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import com.PC_Projects.file_enc_dec.bean.UserDetails;
import com.PC_Projects.file_enc_dec.dao.File_Reader_Writer;
import com.PC_Projects.file_enc_dec.util.HibernateUtil;
import com.PC_Projects.file_enc_dec.util.KeyManager;

/*@WebServlet(urlPatterns = "/lp", loadOnStartup = 1)

initParams = {
        @WebInitParam(name = "UploadFilePathToEncrypt", value = "/home/pradeepkathare/Documents/filesystems/encrypt/"),
        @WebInitParam(name = "maxUploadSize", value = "9900000")
}
*/	

@WebServlet(asyncSupported = false, urlPatterns = { "/fileupload" },

initParams = {
@WebInitParam(name = "FilePathToEncrypt", value = "/home/pradeepkathare/Documents/filesystems/encrypt/"),
@WebInitParam(name = "TempDirs", value = "/home/pradeepkathare/Documents/filesystems/temp/"),
@WebInitParam(name = "FilePathToDecrypt", value = "/home/pradeepkathare/Documents/filesystems/temp/")})

/**
 * Pradeep.kathare
 * dec 5th 2017
 */

public class FileUploadController extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
	/*private File UploadFilePathToEncrypt ;*/
	public Logger logger_ = Logger.getLogger(FileUploadController.class);

	private Session openSession() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
		} catch (Exception ex) {
			logger_.error("Ex-->openSession() : ", ex);
		}
		return session;
	}
	
	/* public void init() throws ServletException {
	        // Configure UploadFilePathToEncrypt.
	        String UploadFilePathToEncryptParam = getServletConfig().getInitParameter("UploadFilePathToEncrypt");
	        if (UploadFilePathToEncryptParam == null) {
	            throw new ServletException("MyServlet 'UploadFilePathToEncrypt' is not configured.");
	        }
	        UploadFilePathToEncrypt = new File(UploadFilePathToEncryptParam);
	        if (!UploadFilePathToEncrypt.exists()) {
	            throw new ServletException("MyServlet 'UploadFilePathToEncrypt' does not exist.");
	        }
	        if (!UploadFilePathToEncrypt.isDirectory()) {
	            throw new ServletException("MyServlet 'UploadFilePathToEncrypt' is not a directory.");
	        }
	        if (!UploadFilePathToEncrypt.canWrite()) {
	            throw new ServletException("MyServlet 'UploadFilePathToEncrypt' is not writeable.");
	        }
	    }*/


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		doPost(request,response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("userNme: " + request.getParameter("userName"));
		System.out.println("password: " + request.getParameter("password"));
		
		String perform = request.getParameter("type");
		
		System.out.println("values of request name :"+"perform : "+perform);
	
		String[] fileNames = filupload(request);
		
			if(null != fileNames[0] && null != fileNames[1] && fileNames[0].equalsIgnoreCase("encrypt"))
			{
				System.out.println("within encrypt");
				//read from temp dir and encrypt data  and write to encrypt folder 
				//Specify location of encrypt folder 
				fileUploadEncrypt(fileNames[0], fileNames[1],);
			}else{
				//read from temp dir and decrypt data  and write to decrypt folder 
				//Specify location of decrypt folder
				System.out.println("within decrypt");
				//fileUploadDecrypt(request, response);
			}
		

	}

	private String[] filupload(HttpServletRequest request) {

		System.out.println("within filupload: ");
		String[] fileNames = new String[10];
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		ServletConfig config = getServletConfig();
		if (!isMultipart) {
			System.out.println("within if: ");
			
		} else {
			System.out.println("within else: ");
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = null;
			int i = 0;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
			Iterator itr = items.iterator();
			
			
			String inputName = "";
			String operationType = null;
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					System.out.println("within if");

					if(item.isFormField()){ 
						inputName = (String)item.getFieldName(); 
						System.out.println("input Name :"+inputName);
						fileNames[i++] = inputName;
							 }
				} else {
					System.out.println("within else of fileupload");
					try {
						String itemName = item.getName();
						System.out.println("file name uploaded"+itemName);
						File savedFile = new File(itemName);
						//write to temp directory
						String t = getServletConfig().getInitParameter("UploadFilePathToEncrypt");
						System.out.println("INIT PARAMETER filePathForEncrypt : "+UploadFilePathToEncrypt);
						
						item.write(new File(UploadFilePathToEncrypt + savedFile.getName()));
						System.out.println("file name uploaded : " + savedFile.getName());
						fileNames[i++] = savedFile.getName();
//						fileNames[i++] = buf.toString();

						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(item!=null){
							
							
						}
					}
				}
			}
		}

		return fileNames;
	}
	// Steps 1 : ToUpload file to particular location(Encrypt)
	// 2 : To encrypt file and store in same location

	/*private void fileUploadDecrypt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub

		// fileupload method
		
		BufferedWriter bw = null;
		try {
			String clearEncryptKey = "1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF";
			String[] fileNames = filupload(request);
			String fileName_To_Encrypt = fileNames[0];
			String fileName_key = fileNames[1];
			File_Reader_Writer filereaderWriter = new File_Reader_Writer();
			//String filepath_to_encrypt = getServletConfig().getInitParameter("filepath_to_encrypt");
			
			String encryptedData =  filereaderWriter.readAndEncrypt( fileName_To_Encrypt,  fileName_key,UploadFilePathToEncrypt);
		
			System.out.println("values in encryptedData : "+encryptedData);
			PrintWriter pw = response.getWriter();
			pw.write("sucess");

		}catch(Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}finally {
			
			if(bw != null) {
				
				bw.close();
			}
		}

	}
*/
	// Steps 1 : ToUpload file to particular location(Decrypt)
	// 2 : To encrypt file and store in same location

	private void fileUploadEncrypt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("within fileUploadEncrypt :");
		BufferedWriter bw = null;
		try {
			String clearEncryptKey = "1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF";
			
			
			/*String fileName_To_Encrypt = fileNames[0];
			String fileName_key = fileNames[1];*/
			File_Reader_Writer filereaderWriter = new File_Reader_Writer();
			System.out.println("fileName_To_Encrypt : "+fileName_To_Encrypt +"fileName_key :"+fileName_key);
//			String encryptedData =  filereaderWriter.readAndEncrypt( fileName_To_Encrypt,  fileName_key);
		
			//String filepath_to_encrypt = getServletConfig().getInitParameter("UploadFilePathToEncrypt");
			
			String encryptedData =  filereaderWriter.readAndEncrypt( fileName_To_Encrypt,  fileName_key,UploadFilePathToEncrypt);
		
			
			System.out.println("values in encryptedData : "+encryptedData);
			PrintWriter pw = response.getWriter();
			pw.write("sucess");

		}catch(Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}finally {
			
			if(bw != null) {
				
				bw.close();
			}
		}
	
	}

	
}
