package com.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlannerServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String strBudget = req.getParameter("budget");
		String strNoOfDays = req.getParameter("noOfDays");
		String startLocation = req.getParameter("startLocation");
		
		System.out.println("User total Budget is " + Double.valueOf(strBudget) + " and " 
				+ Integer.valueOf(strNoOfDays) + " days " 
				+ "at start location " + startLocation);
		//super.doPost(req, resp);
	}

}
