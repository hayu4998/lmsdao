package com.gcit.lms.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.dao.*;
import com.gcit.lms.entity.*;
import com.gcit.lms.ui.TextUI;

public class Service {

	private ConnectionUtil connUtil = new ConnectionUtil();
	protected static TextUI UI = TextUI.getInstance();
	protected Connection conn = null;
	
	
	
	/**
	 * Connection to database
	 */
	public void getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				conn = connUtil.getConnection();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Connection failure");
			e.printStackTrace();
			return;
		}
		
	}
	/**
	 * Close Connection
	 */
	public void closeConnection() {
		// establish connection
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	// Read from all tables w/ or w/o input
//	public <T extends BaseDAO, E extends Entity> E  Read(T DAO, String type, Boolean expectInput)
//			throws ClassNotFoundException, SQLException {
//
//		return UI.uiShowGenericList(DAO.readAll(), type, expectInput);
//
//	}

	// Delete from all tables
//	public <T extends BaseDAO> void Deletion(T dao, String pk) throws ClassNotFoundException, SQLException {
//		
//		dao.Delete(pk);
//	}

	// Non relational add
	public <T extends BaseDAO, E extends Entity> void nonRelationalAdd(T DAO, E entity)
			throws ClassNotFoundException, SQLException {
		DAO.add(entity);
	}

	// Update for all tables
	public <T extends BaseDAO, E extends Entity> void nonRelationalUpdate(E entity, T DAO, String field,
			String valueToBe) throws ClassNotFoundException, SQLException {
		if (valueToBe == null) {
			return;
		} else {
			DAO.update(entity, field);
		}
	}
}
