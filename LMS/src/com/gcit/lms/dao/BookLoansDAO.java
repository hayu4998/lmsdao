package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.BookLoans;

/**
 * @author Jason
 *
 */
public class BookLoansDAO extends BaseDAO<BookLoans> {

	public BookLoansDAO(Connection connection) {
		super(connection);
	}

	public String add(BookLoans bookloan) throws ClassNotFoundException, SQLException {
		
		return saveReturnString("insert into tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) values (?,?,?,now(),now() + interval 7 day)", 
			new Object[] {
					bookloan.getBook().getBookId(),
					bookloan.getBranch().getLibraryBranchId(),
					bookloan.getBorrower().getCardId()
			}
		);
	}
	
	public void borrowBook(Object[] objlist)throws ClassNotFoundException, SQLException {
		
		save("insert into tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) values (?,?,?,now(),now() + interval 7 day)", 
			objlist
		);
	}

	public void update(BookLoans bookLoans, String field) throws ClassNotFoundException, SQLException {
		
		Object[] fieldlist = null;
		String sql = null;
		
		if(field.contentEquals("dateIn")) {
			sql = "update tbl_book_loans set dateIn = now() where bookId =? and branchId = ? and CardNo = ? and dateOut = ? ";
			fieldlist = new Object[] {
					
					bookLoans.getBook().getBookId(),
					bookLoans.getBranch().getLibraryBranchId(),
					bookLoans.getBorrower().getCardId(),
					bookLoans.getLoanedDateOut()
					}; 
		}else if(field.contentEquals("dueDate")) {
			sql = "update tbl_book_loans set dueDate = dueDate + interval 7 day where bookId = ? and branchId = ? and CardNo = ? and dateOut = ?";
			fieldlist = new Object[] {
					bookLoans.getBook().getBookId(),
					bookLoans.getBranch().getLibraryBranchId(),
					bookLoans.getBorrower().getCardId(),
					bookLoans.getLoanedDateOut()}; 
		}
		
		save(sql,fieldlist);
	}

	public void delete(BookLoans bookLoans) throws ClassNotFoundException, SQLException {
		
		saveWithID("delete from tbl_book_loans where bookId =? and branchId = ? and dateOut = ?", 
			new Object[]{
					bookLoans.getBook().getBookId(),
					bookLoans.getBranch().getLibraryBranchId(),
					bookLoans.getBorrower().getCardId()
			}	
		);
	}
	
	public List<BookLoans> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_book_loans", null);
	}
	
	public List<BookLoans> readBookLoansByID(String field, Integer pk) throws ClassNotFoundException, SQLException{
		return read("select * from tbl_book_loans where "+field+" = ? and dateIn is null",new Object[] {pk});
	}
	
	public List<BookLoans> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<BookLoans> BookLoanss = new ArrayList<>();
		
		BookDAO bdao = new BookDAO(conn);
		LibraryBranchDAO bhdao = new LibraryBranchDAO(conn);
		BorrowerDAO bordao = new BorrowerDAO(conn);
		
		while(rs.next()){
			BookLoans BookLoans = new BookLoans();
			//set dates
			BookLoans.setLoanedDateIn(rs.getString("dateIn"));
			BookLoans.setLoanedDateOut(rs.getString("dateOut"));
			BookLoans.setLoanedDueDate(rs.getString("dueDate"));
			
			//set Borrower Object
			BookLoans.setBorrower(
					bordao.readFirstLevel(
							"select * from tbl_borrower where cardNo = ?", 
							new Object[] {
								rs.getInt("cardNo")
							}
					).get(0)
			);
			
			//set Book Object 
			BookLoans.setBook(
					bdao.readFirstLevel(
							"select * from tbl_book where bookId = ?", 
							new Object[] {
									rs.getInt("bookId")
							}
					).get(0)
			);
			
			//set Branch Object
			BookLoans.setBranch(
					bhdao.readFirstLevel(
							"select * from tbl_library_branch where branchId = ?", 
							new Object[] {
									rs.getInt("branchId")
							}
					).get(0)
			);
			
			//add to the list
			BookLoanss.add(BookLoans);
		}
		return BookLoanss;
	}
	
	public List<BookLoans> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<BookLoans> BookLoanss = new ArrayList<>();
		while(rs.next()){
			BookLoans BookLoans = new BookLoans();
			
			//set dates
			BookLoans.setLoanedDateIn(rs.getString("dateIn"));
			BookLoans.setLoanedDateOut(rs.getString("dateOut"));
			BookLoans.setLoanedDueDate(rs.getString("dueDate"));
			
			//add to the list
			BookLoanss.add(BookLoans);
		}
		return BookLoanss;
	}


}
