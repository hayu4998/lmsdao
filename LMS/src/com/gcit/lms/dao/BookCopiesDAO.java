/**
 * 
 */
package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.BookCopies;

/**
 * @BookCopies Jason
 *
 */
public class BookCopiesDAO extends BaseDAO<BookCopies> {

	public BookCopiesDAO(Connection connection) {
		super(connection);
	}

	public String add(BookCopies BookCopies) throws ClassNotFoundException, SQLException {
		return saveReturnString("insert into tbl_book_copies (bookId, branchId, noOfCopies) values (?,?,?)", 
			new Object[]{
					BookCopies.getBook().getBookId(), 
					BookCopies.getBranch().getLibraryBranchId(), 
					BookCopies.getNoOfCopies()}
		);
	}

	public void update(BookCopies BookCopies, String field) throws ClassNotFoundException, SQLException {
		saveWithID("update tbl_book_copies set " + field + " = ? where bookId = ? and branchId = ?", 
			new Object[]{
					BookCopies.getNoOfCopies(), 
					BookCopies.getBook().getBookId(),
					BookCopies.getBranch().getLibraryBranchId()
					}
		);
	}
	
	public void borrowReturnFunction( String sign, Integer bookId, Integer branchId) throws ClassNotFoundException, SQLException {
		saveWithID("update tbl_book_copies set noOfCopies = noOfCopies" + sign + " 1 where bookId=? and branchId = ?",
				new Object[] {
						bookId,
						branchId
				}
		);
	}

	public void delete(BookCopies bookCopies) throws ClassNotFoundException, SQLException {
		saveWithID("delete from tbl_book_copies where concate bookId = ? branchId = ?", 
			new Object[]{
					bookCopies.getBook().getBookId(),
					bookCopies.getBranch().getLibraryBranchId()
			}
		);
	}
	
	public List<BookCopies> readAllBookCopiesFromBranch(Integer branchId) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_book_copies where branchId = ? and noOfCopies >0 ", new Object[] {branchId});
	}
	
	@Override
	public List<BookCopies> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_book_copies",null);
	}
	
//	public BookCopies readBookCopiesFromID(String pk) throws ClassNotFoundException, SQLException {
//		return read("select * from tbl_book_copies where concat(bookId,branchId) = ?", new Object[] {pk}).get(0);
//	}
	
	public List<BookCopies> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<BookCopies> BookCopiess = new ArrayList<>();
		
		BookDAO bdao = new BookDAO(conn);
		LibraryBranchDAO bhdao = new LibraryBranchDAO(conn);
		
		while(rs.next()){
			BookCopies BookCopies = new BookCopies();
			
			//set # of Copies
			BookCopies.setNoOfCopies(rs.getInt("noOfCopies"));
			
			//set Book Object 
			BookCopies.setBook(
					bdao.readFirstLevel(
							"select * from tbl_book where bookId = ?", 
							new Object[] {
									rs.getInt("bookId")
							}
					).get(0)
			);
			//System.out.println("successful inject book");
			//set Branch Object
			BookCopies.setBranch(
					bhdao.readFirstLevel(
							"select * from tbl_library_branch where branchId = ?", 
							new Object[] {
									rs.getInt("branchId")
							}
					).get(0)
			);
			//System.out.println("successfully inject branch");
			//add to the list
			BookCopiess.add(BookCopies);
		}
		return BookCopiess;
	}
	
	public List<BookCopies> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<BookCopies> BookCopiess = new ArrayList<>();
		while(rs.next()){
			BookCopies BookCopies = new BookCopies();
			
			//set # of Copies
			BookCopies.setNoOfCopies(rs.getInt("noOfCopies"));
			
			
			//add to the list
			BookCopiess.add(BookCopies);
		}
		return BookCopiess;
	}

	public BookCopies librarianReadAllBookCopiesFromBranch(Integer bookId) throws ClassNotFoundException, SQLException {
		List<BookCopies> bc = read("select * from tbl_book_copies where branchId = ?", new Object[] {bookId});
		if(bc.size() == 0) {
			return null;
		}else {
			return bc.get(0);
		}
	}

}
