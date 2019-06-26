package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.LibraryBranch;

public class LibrarianService extends Service{

	private static LibrarianService librarianServiceInstance = null;
	
	private LibrarianService() {
		getConnection();
	}
	
	public static LibrarianService getInstance() {
		if(librarianServiceInstance == null) {
			librarianServiceInstance = new LibrarianService();
		}
		return librarianServiceInstance;
	}
	
	private LibraryBranch chooseBranch() {
		LibraryBranchDAO lbdao = new LibraryBranchDAO(conn);

		LibraryBranch branch = null;
		List<LibraryBranch> branchList = null;

		// populate Branch List and choose one
		try {
			branchList = lbdao.readAll();
			branch = UI.selectBranchFromList(branchList);
			// if quit operation
			if (branch == null) {
				conn.rollback();
				return null;
			}
			return branch;
		} catch (ClassNotFoundException e) {
			System.out.println("Connection Issue");
			return null;
		} catch (SQLException e) {
			System.out.println("Invalid SQL");
			e.printStackTrace();
			return null;
		}
	}
	
	private void updateLibraryInfo(LibraryBranch branch) {
		
		LibraryBranchDAO lbdao = new LibraryBranchDAO(conn);
		
		
		// update library branch info
		String name = UI.librarianUpdateBranch("name");
		String address = UI.librarianUpdateBranch("address");

		// set values and update database
		try {
			if (name != null) {
				branch.setLibraryBranchName(name);
				System.out.println("BranchID = " + branch.getLibraryBranchId());
				System.out.println("New name: " + branch.getLibraryBranchName());
				lbdao.update(branch, "branchName");
			}
			if (address != null) {
				branch.setLibraryBranchAddress(address);
				System.out.println("New address: " + branch.getLibraryBranchAddress());
				lbdao.update(branch, "branchAddress");
			}
			conn.commit();
			System.out.println("Change commited");
		} catch (ClassNotFoundException e) {
			System.out.println("Connection Issue");

		} catch (SQLException e) {
			System.out.println("Invalid SQL");
			e.printStackTrace();

		}
	}
	
	private boolean updateNoOfCopies(LibraryBranch branch) {
		// update no. of copies of the choosing book;
		try {
			
			BookCopiesDAO bcdao = new BookCopiesDAO(conn);
			BookDAO bdao = new BookDAO(conn);
			//get entire book list
			Book book = UI.uiShowGenericList(bdao.readAll(), "book", true);
			
			// if quit operation
			if (book == null) {
				conn.rollback();
				return false;
			}
			
			//get desired book copy
			BookCopies bookCopies = bcdao.librarianReadAllBookCopiesFromBranch(book.getBookId());
			//if none exist
			if(bookCopies == null) {
				// obtain new No. of copies
				Integer newNoOfCopie = UI.librarianModifyBookCopies(0);
				// set up the new book copy entity
				BookCopies newBookCopie = new BookCopies();
				newBookCopie.setBook(book);
				newBookCopie.setBranch(branch);
				newBookCopie.setNoOfCopies(newNoOfCopie);
				// add book copy
				bcdao.add(newBookCopie);
			}else {
				
				// obtain new No. of copies
				Integer newNoOfCopie = UI.librarianModifyBookCopies(bookCopies.getNoOfCopies());
				bookCopies.setNoOfCopies(newNoOfCopie);
				
				// update book copies
				bcdao.update(bookCopies, "noOfCopies");
			}
			
			conn.commit();
		} catch (ClassNotFoundException e) {
			System.out.println("Connection Issue");

		} catch (SQLException e) {
			System.out.println("Invalid SQL");
			e.printStackTrace();

		}
		return true;
	}
	
	// librarian functionality
	public void startService() {
		// connection
		getConnection();
		
		while (true) {

			// Choose from Librarian welcome interface
			Integer opt = UI.librarianWelcome();
			if (opt == 2 || opt == 0) {
				break;
			} else {
				//get branch
				LibraryBranch branch = chooseBranch();
				if(branch == null) {
					continue;
				}
				while (true) {
					// Select Operation to the Library Branch
					opt = UI.librarianChooseFunction();
					
					if (opt == 3 || opt == 0) {
						//quit
						break;
					} else if (opt == 1) {
						//update library info
						updateLibraryInfo(branch);

					} else {
						//update copies
						if(!updateNoOfCopies(branch)) {
							continue;
						}
					}

				}

			}

		}
		closeConnection();
	}

}
