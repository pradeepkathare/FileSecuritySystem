package com.PC_Projects.file_enc_dec.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
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
import org.hibernate.Session;

import com.PC_Projects.file_enc_dec.dao.File_Reader_Writer;
import com.PC_Projects.file_enc_dec.util.HibernateUtil;
import com.PC_Projects.file_enc_dec.util.KeyManager;

@WebServlet(urlPatterns = "/lp", loadOnStartup = 1)
public class LoginController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Logger logger_ = Logger.getLogger(LoginController.class);

	private Session openSession() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
		} catch (Exception ex) {
			logger_.error("Ex-->openSession()", ex);
		}
		return session;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("userNme: " + request.getParameter("userName"));
		System.out.println("password: " + request.getParameter("password"));
		String perform = request.getParameter("perform");

		if (null == perform) {
			// TODO:REDIrect to error page without proper messgae
			System.out.println("perofrom is null");
			login(request, response);
		}
		if (perform.equalsIgnoreCase("encrypt")) {
			System.out.println("within encrypt");
			fileUploadEncrypt(request, response);
		} else if (perform.equalsIgnoreCase("decrypt")) {
			fileUploadDecrypt(request, response);
		} else {
			login(request, response);
		}

	}

	private String[] filupload(HttpServletRequest request) {

		String[] fileNames = new String[10];
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (!isMultipart) {

		} else {
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
			StringBuffer buf = new StringBuffer();
			String bufcontent[] = new String[2];
			int j = 0;
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
				} else {
					try {
						String itemName = item.getName();
						System.out.println("file name uploaded"+itemName);
						File savedFile = new File(itemName);

						/*while ((item.getInputStream().read() != -1)) {

							buf.append(item.getInputStream().read());
						}
*/
						item.write(new File("E://pradeep//encrypt" + savedFile.getName()));
						System.out.println("file name uploaded" + savedFile.getName());
						fileNames[i++] = savedFile.getName();
//						fileNames[i++] = buf.toString();

						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return fileNames;
	}
	// Steps 1 : ToUpload file to particular location(Encrypt)
	// 2 : To encrypt file and store in same location

	private void fileUploadDecrypt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub

		// fileupload method
		
		BufferedWriter bw = null;
		try {
			String clearEncryptKey = "1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF";
			String[] fileNames = filupload(request);
			String fileName_To_Encrypt = fileNames[0];
			String fileName_key = fileNames[1];
			File_Reader_Writer filereaderWriter = new File_Reader_Writer();
			
			String encryptedData =  filereaderWriter.readAndEncrypt( fileName_To_Encrypt,  fileName_key);
		
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

	// Steps 1 : ToUpload file to particular location(Decrypt)
	// 2 : To encrypt file and store in same location

	private void fileUploadEncrypt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub

		BufferedWriter bw = null;
		try {
			String clearEncryptKey = "1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF";
			String[] fileNames = filupload(request);
			String fileName_To_Encrypt = fileNames[0];
			String fileName_key = fileNames[1];
			File_Reader_Writer filereaderWriter = new File_Reader_Writer();
			
			String encryptedData =  filereaderWriter.readAndEncrypt( fileName_To_Encrypt,  fileName_key);
		
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

	public String login(HttpServletRequest request, HttpServletResponse response) {

		String usr = request.getParameter("userName");
		logger_.info("userName: " + usr);
		String pas = request.getParameter("password");
		logger_.info("password: " + pas);

		try {
			boolean validUser = authanticateUser(usr, pas);
			if (validUser) {
				/*
				 * String htmlout = "<html> <body bgcolor=yellow>" +
				 * "<h1><center><font color=blue size=30pt>" + "WELCOME " + "	" + usr + " ..."
				 * + "</font></center><h1></body></html>";
				 */
				PrintWriter out = response.getWriter();
				// out.println(htmlout);
				RequestDispatcher rd = request.getRequestDispatcher("/FileUpload.html");
				rd.include(request, response);
				out.close();
			} else {
				String htmlout = "<html> <body bgcolor=red>" + "<h1><center><font color=blue size=30pt>"
						+ "PLESE ENTER VALID USER NAME " + "</font></center><h1></body></html>";
				PrintWriter out = response.getWriter();
				out.println(htmlout);

				// RequestDispatcher rd = request.getRequestDispatcher("/login.html");

				response.sendRedirect("/login.html");

				// rd.include(request, response);

				out.close();
			}

		} catch (Exception ex) {
			logger_.info("Exception authanticateUser() Class " + ex.getClass() + "\n Cause " + ex.getCause()
					+ "\n Message " + ex.getMessage());
		}

		return "";

	}

	@SuppressWarnings("deprecation")
	public Boolean authanticateUser(String userName, String password) throws Exception {
		logger_.info("authanticateUser() ");
		boolean vaildUser = false;
		if (null == userName || null == password) {
			logger_.info("UserName or Password is Null.");
			throw new Exception("UserName or Password is Null.");
		}
		Session session = openSession();
		try {
			String queryString = "SELECT LOCATION, TRANSNO, USERNAME, PASSWORD, COMMENTS, USEREXIT1, USEREXIT2"
					+ " FROM T_LOGIN01" + " WHERE LOCATION = '23'  AND USERNAME = '" + userName + "'"
					+ " AND PASSWORD = '" + password + "'";
			logger_.info("queryString: " + queryString);
			List<?> defau = session.createSQLQuery(queryString).addScalar("LOCATION", Hibernate.STRING)
					.addScalar("TRANSNO", Hibernate.STRING).addScalar("USERNAME", Hibernate.STRING)
					.addScalar("PASSWORD", Hibernate.STRING).addScalar("COMMENTS", Hibernate.STRING)
					.addScalar("USEREXIT1", Hibernate.STRING).addScalar("USEREXIT2", Hibernate.STRING).list();
			logger_.info("After executing the Query..." + defau.size());
			Iterator<?> iterator = defau.iterator();

			while (iterator.hasNext()) {
				Object[] columns = (Object[]) iterator.next();
				String dbUserName = (String) columns[2];
				String dbPassword = (String) columns[3];
				if (dbUserName.equals(userName) && dbPassword.equals(password)) {
					vaildUser = true;
					logger_.info("vaildUser = true");
				}
			}
		} catch (Exception ex) {
			logger_.info("Exception authanticateUser() Class " + ex.getClass() + "\n Cause " + ex.getCause()
					+ "\n Message " + ex.getMessage());
		} finally {
			session.close();
		}
		return vaildUser;
	}
}
