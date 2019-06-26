package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Borrower;

public class BorrowerDAO extends BaseDAO<Borrower>{

	public BorrowerDAO(Connection connection) {
		super(connection);
	}

	public Integer add(Borrower borrower) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_borrower (name,address,phone) values (?,?,?)", 
				new Object[]{
						borrower.getBorrowerName(),
						borrower.getBorrowerAddress(),
						borrower.getBorrowerPhone()
				}
		);
	}

	public void update(Borrower borrower,String field) throws ClassNotFoundException, SQLException {
		String field1 = null;
		if(field.contentEquals("name")) {
			field1 = borrower.getBorrowerName();
		}
		if(field.contentEquals("address")) {
			field1 = borrower.getBorrowerAddress();
		}
		if(field.contentEquals("phone")) {
			field1 = borrower.getBorrowerPhone();
		}
		save(
				"update tbl_borrower set " + field+ " = ? where cardNo = ?", 
				new Object[]{field1, borrower.getCardId()}
		);
	}

	public void delete(Borrower borrower) throws ClassNotFoundException, SQLException {
		
		//check if exist book records
		BookLoansDAO bdao = new BookLoansDAO(conn);
		if(bdao.readBookLoansByID("CardNo",borrower.getCardId()).size() != 0) {
			System.out.println("The Borrower has unreturned book, do you still want to delete it?[Y/n]");
			
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		save(
				"delete from tbl_borrower where cardNo = ?", 
				new Object[]{borrower.getCardId()}
		);
		
	}
	
	public List<Borrower> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_borrower", null);
	}
	
	public Borrower selectBorrowerByID(Integer cardNo) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_borrower where cardNo = ?", new Object[] {cardNo}).get(0);
	}
	
	public List<Borrower> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Borrower> borrowers = new ArrayList<>();
		BookLoansDAO bldao = new BookLoansDAO(conn);
		while(rs.next()){
			Borrower borrower = new Borrower();
			
			borrower.setCardId(rs.getInt("cardNo"));
			borrower.setBorrowerName(rs.getString("name"));
			borrower.setBorrowerAddress(rs.getString("address"));
			borrower.setBorrowerPhone(rs.getString("phone"));
			
			//generate bookLoans list
			borrower.setBookLoans(
					bldao.readFirstLevel(
							"select * from tbl_book_loans bl where bl.cardNo = ?", 
							new Object[] {
									borrower.getCardId()
							}
					)
			);
			
			//add to list
			borrowers.add(borrower);
		}
		return borrowers;
	}
	
	public List<Borrower> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Borrower> borrowers = new ArrayList<>();
		while(rs.next()){
			Borrower borrower = new Borrower();
			
			borrower.setCardId(rs.getInt("cardNo"));
			borrower.setBorrowerName(rs.getString("name"));
			borrower.setBorrowerAddress(rs.getString("address"));
			borrower.setBorrowerPhone(rs.getString("phone"));
			
			borrowers.add(borrower);
		}
		return borrowers;
	}

}
