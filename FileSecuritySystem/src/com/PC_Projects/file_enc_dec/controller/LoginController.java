package com.PC_Projects.file_enc_dec.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.PC_Projects.file_enc_dec.bean.UserDetails;
import com.PC_Projects.file_enc_dec.util.HibernateUtil;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public Logger logger_ = Logger.getLogger(LoginController.class);
  
	private Session openSession() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
		} catch (Exception ex) {
			logger_.error("Ex-->openSession() : ", ex);
		}
		return session;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		login(request, response);
		return;
	}

	public String login(HttpServletRequest request, HttpServletResponse response) {

		String usr = request.getParameter("userName");
		logger_.info("userName: " + usr);
		String pas = request.getParameter("password");
		logger_.info("password: " + pas);

		try {
			boolean validUser = authanticateUser(usr, pas);
			if (validUser) {
				
				PrintWriter out = response.getWriter();
				// out.println(htmlout);
				RequestDispatcher rd = request.getRequestDispatcher("/FileUploadV2.html");
				rd.include(request, response);
				out.close();
			} else {
				String htmlout = "<html> <body bgcolor=red>" + "<br/><h4><center><font color=red size=5pt>"
						+ "  INVALID USER NAME " + "</font></center><h3></body></html>";
				PrintWriter out = response.getWriter();
				out.println(htmlout);

				 RequestDispatcher rd = request.getRequestDispatcher("index.html");

				//response.sendRedirect("index.html");

				 rd.include(request, response);

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
		logger_.info("with in  authanticateUser() :  ");
		UserDetails userdetails = null;
		
		boolean vaildUser = false;
		
		if (null == userName || null == password) {
			logger_.info("UserName or Password is Null.");
			throw new Exception("UserName or Password is Null.");
		}
		
		
		Session session = openSession();
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT  user_name ")
						.append("FROM AUTHORISED_USERS WHERE user_name = '").append(userName).append("'");
			
					
			logger_.info("queryString: " + queryString);
			 //using native sql 
	        Query query = session.createSQLQuery(queryString.toString());
	        List list = query.list();
	        for (Object user : list) {
	            if (user != null ) {
	                //user_name =  user;
	                
	                if(userName.equals(user)) {
	                	System.out.println("VALID USER AND USERNAME : " +user);
	                	vaildUser = true;
	                }
	               }
	            }

			
		} catch (Exception ex) {
			//forward to error.jsp
			logger_.info("Exception authanticateUser() Class " + ex.getClass() + "\n Cause " + ex.getCause()
					+ "\n Message " + ex.getMessage());
			
		} finally {
			session.close();
		}
		return vaildUser;
	}
	
}
