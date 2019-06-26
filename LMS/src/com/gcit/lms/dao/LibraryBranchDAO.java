/**
 * 
 */
package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.LibraryBranch;

/**
 * @branch Jason
 *
 */
public class LibraryBranchDAO extends BaseDAO<LibraryBranch> {

	public LibraryBranchDAO(Connection connection) {
		super(connection);
	}

	public Integer add(LibraryBranch branch) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_library_branch (branchName,branchAddress) values (?,?)", 
				new Object[]{branch.getLibraryBranchName(), branch.getLibraryBranchAddress()}
		);
	}

	public void update(LibraryBranch branch, String field) throws ClassNotFoundException, SQLException {
		Object[] fieldlist = null;
		if(field.contentEquals("branchName")) {
			fieldlist = new Object[] {
					branch.getLibraryBranchName(),
					branch.getLibraryBranchId()
			};
			
		}
		if(field.contentEquals("branchAddress")) {
			fieldlist = new Object[] {
					branch.getLibraryBranchAddress(),
					branch.getLibraryBranchId()
			};
			
		}
		saveWithID(
				"update tbl_library_branch set "+ field + " = ? where branchId = ?", 
				fieldlist
		);
	}

	public void delete(LibraryBranch libraryBranch) throws ClassNotFoundException, SQLException {
		
		//check if exist book loan records
		BookLoansDAO bdao = new BookLoansDAO(conn);
		if(bdao.readBookLoansByID("branchId",libraryBranch.getLibraryBranchId()).size() != 0) {
			System.out.println("The Branch has borrowing record, do you still want to delete it?[Y/n]");
			
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		save(
				"delete from tbl_library_branch where branchId = ?", 
				new Object[]{libraryBranch.getLibraryBranchId()}
		);
	}
	
	public List<LibraryBranch> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_library_branch", null);
	}
	
	public LibraryBranch readLibraryBranchByID(String pk) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_library_branch where branchId = ?", new Object[] {pk}).get(0);
	}
	
	public List<LibraryBranch> readLibraryBranchExcludsive(Book book) throws ClassNotFoundException, SQLException{
		return read("select * from tbl_library_branch where branchId not in (select distinct branchId from tbl_book_copies where bookId = ?)", new Object[] {book.getBookId()});
		
	}
	
	public List<LibraryBranch> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<LibraryBranch> branchs = new ArrayList<>();
		
		BookCopiesDAO bcdao = new BookCopiesDAO(conn);
		BookLoansDAO bldao = new BookLoansDAO(conn);
		
		while(rs.next()){
			LibraryBranch branch = new LibraryBranch();
			
			branch.setLibraryBranchId(rs.getInt("branchId"));
			branch.setLibraryBranchName(rs.getString("branchName"));
			branch.setLibraryBranchAddress(rs.getString("branchAddress"));
			
			//set BookCopies List
			branch.setBookCopies(
					bcdao.readFirstLevel(
							"select * from tbl_book_copies bc where bc.branchId = ?", 
							new Object[]{branch.getLibraryBranchId()}
					)
			);
			
			//set BookLoans List
			branch.setBookLoans(
					bldao.readFirstLevel(
							"select * from tbl_book_loans bl where bl.branchId = ?", 
							new Object[]{branch.getLibraryBranchId()}
					)
			);
			
			//add to the list
			branchs.add(branch);
		}
		return branchs;
	}
	
	public List<LibraryBranch> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<LibraryBranch> branchs = new ArrayList<>();
		while(rs.next()){
			LibraryBranch branch = new LibraryBranch();
			
			branch.setLibraryBranchId(rs.getInt("branchId"));
			branch.setLibraryBranchName(rs.getString("branchName"));
			branch.setLibraryBranchAddress(rs.getString("branchAddress"));
			
			branchs.add(branch);
		}
		return branchs;
	}

}
