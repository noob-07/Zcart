package com.Zcart.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Zcart.Handler.RequestHandler;
import com.Zcart.Utility.Utility;
import com.Zcart.exception.*;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MainServlet
 */
// @WebServlet("/ApplicationController")
public class ApplicationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	RequestHandler requesthandler = null;

	Utility util = new Utility();

	enum mapurl {
		signin, Signup, logout
	}

	public ApplicationController() {
		// TODO Auto-generated constructor stub
	}

//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		requesthandler=new RequestHandler();
//		String id = null;
//		String pid=null;
//		String urlpath = request.getRequestURI();
//		System.out.println(urlpath);
//		urlpath = urlpath.replaceFirst("/", "");
//		String[] url = urlpath.split("/");
//		int arrlength = url.length;
//		System.out.println(Arrays.toString(url));
//		String className = null;
//
//		Pattern p = Pattern.compile(util.regex);
//		Matcher m = p.matcher(url[arrlength - 1]);
//		
//		if (url[1].equalsIgnoreCase("user")) {
//			id = url[2];
//		}
//		if (m.matches())
//			className = util.getClassName(url[url.length - 2]);
//		else {
//			className = util.getClassName(url[url.length - 1]);
//
//		}
//		if (className.equalsIgnoreCase("signup"))
//			className = "User";
//		try {
////			Cookie cookie=new Cookie("username",request.getParameter("Name"));
////			Cookie cookie2=new Cookie("user_password",request.getParameter("Password"));
////			Cookie[] cookies=request.getCookies();
////			response.addCookie(cookies[0]);
////			response.addCookie(cookies[1]);
//			JsonObject result=requesthandler.getRequestType(className, request, response, id,pid);
//		} catch (DatabaseException excpetion) {
//			excpetion.printStackTrace();
//		} catch (AuthenticationFailedException excpetion) {
//			excpetion.printStackTrace();
//		}
//		
//	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requesthandler = new RequestHandler();
		String id = null;
		String pid = null,res=null;
		String urlpath = request.getRequestURI();
		System.out.println(urlpath);
		urlpath = urlpath.replaceFirst("/", "");
		String[] url = urlpath.split("/");
		int arrlength = url.length;
		System.out.println(Arrays.toString(url));
		String className = null;

		Pattern p = Pattern.compile(util.regex);
		Matcher m = p.matcher(url[arrlength - 1]);
		
		if (url[1].equalsIgnoreCase("user")) {
			id = url[2];
		}
		if (m.matches()) {
			pid=url[arrlength-1];
			className = util.getClassName(url[url.length - 2]);
		}
		else {
			className = util.getClassName(url[url.length - 1]);

		}

		if (className.equalsIgnoreCase("signup"))
			className = "User";

		try {
//			Cookie cookie=new Cookie("username",request.getParameter("Name"));
//			Cookie cookie2=new Cookie("user_password",request.getParameter("Password"));
//			Cookie[] cookies=request.getCookies();
//			response.addCookie(cookies[0]);
//			response.addCookie(cookies[1]);
			
			String result=requesthandler.getRequestType(className, request, response, id,pid);
			//res=result.toString();
			response.setStatus(200);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(result);	
			out.flush();
			//response.getWriter().println(res);
			
		} catch (DatabaseException excpetion) {
			excpetion.printStackTrace();
		} catch (AuthenticationFailedException excpetion) {
			excpetion.printStackTrace();
		}
		
	}
	
//	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		requesthandler=new RequestHandler();
//		String id = null;
//		String pid = null;
//		String urlpath = request.getRequestURI();
//		System.out.println(urlpath);
//		urlpath = urlpath.replaceFirst("/", "");
//		String[] url = urlpath.split("/");
//		int arrlength = url.length;
//		System.out.println(Arrays.toString(url));
//		String className = null;
//
//		Pattern p = Pattern.compile(util.regex);
//		Matcher m = p.matcher(url[arrlength - 1]);
//		
//		if (url[1].equalsIgnoreCase("user")) {
//			id = url[2];
//		}
//		if (m.matches()) {
//			pid=url[arrlength-1];
//			className = util.getClassName(url[url.length - 2]);
//		}
//		else {
//			className = util.getClassName(url[url.length - 1]);
//
//		}
//		try {
//		requesthandler.getRequestType(className,request,response,id,pid);
//		}catch (DatabaseException excpetion) {
//			excpetion.printStackTrace();
//		}catch (AuthenticationFailedException excpetion) {
//			excpetion.printStackTrace();
//		}
//	
//	}
//	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		requesthandler=new RequestHandler();
//		String id = null;
//		String pid = null;
//		String urlpath = request.getRequestURI();
//		System.out.println(urlpath);
//		urlpath = urlpath.replaceFirst("/", "");
//		String[] url = urlpath.split("/");
//		int arrlength = url.length;
//		System.out.println(Arrays.toString(url));
//		String className = null;
//
//		Pattern p = Pattern.compile(util.regex);
//		Matcher m = p.matcher(url[arrlength - 1]);
//		
//		if (url[1].equalsIgnoreCase("user")) {
//			id = url[2];
//		}
//		if (m.matches()) {
//			pid=url[arrlength-1];
//			className = util.getClassName(url[url.length - 2]);
//		}
//		else {
//			className = util.getClassName(url[url.length - 1]);
//
//		}
//		try {
//		requesthandler.getRequestType(className,request,response,id,pid);
//		}catch (DatabaseException excpetion) {
//			excpetion.printStackTrace();
//		}catch (AuthenticationFailedException excpetion) {
//			excpetion.printStackTrace();
//		}
//	
//	}
}
