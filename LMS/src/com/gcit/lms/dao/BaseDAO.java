package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public abstract class BaseDAO<T> {
	
	Scanner in = new Scanner(System.in);
	public Connection conn = null;
	
	public BaseDAO(Connection connection){
		this.conn = connection;
	}
	
	
	private void populatePrepStatement(Object[] vals, PreparedStatement pstmt) throws SQLException {
		if(vals!=null){
			Integer count = 1;
			for(Object o: vals){
				pstmt.setObject(count, o);
				count++;
			}
		}
	}
	
	public String saveReturnString(String sql, Object[] vals) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		populatePrepStatement(vals, pstmt);
		pstmt.execute();
		ResultSet rs = pstmt.getGeneratedKeys();
		while(rs.next()){
			return rs.getString(1);
		}
		return null;
	}
	
	public void save(String sql, Object[] vals) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		populatePrepStatement(vals, pstmt);
		pstmt.execute();
	}
	
	public Integer saveWithID(String sql, Object[] vals) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		populatePrepStatement(vals, pstmt);
		pstmt.execute();
		
		ResultSet rs = pstmt.getGeneratedKeys();
		while(rs.next()){
			return rs.getInt(1);
		}
		return null;
	}
	
	public List<T> read(String sql, Object[] vals) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = conn.prepareStatement(sql);
		populatePrepStatement(vals, pstmt);
		return extractData(pstmt.executeQuery());
	}
	
	public abstract List<T> extractData(ResultSet rs) throws SQLException, ClassNotFoundException;
	
	public List<T> readFirstLevel(String sql, Object[] vals) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = conn.prepareStatement(sql);
		populatePrepStatement(vals, pstmt);
		return extractDataFirstLevel(pstmt.executeQuery());
	}

	public abstract List<T> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException;
	
	public abstract void delete(T type) throws ClassNotFoundException, SQLException;
	
	public abstract void update(T type, String field) throws ClassNotFoundException, SQLException;
	
	public abstract Object add(T type)throws ClassNotFoundException, SQLException;

	public abstract List<T> readAll()throws ClassNotFoundException, SQLException;

}
