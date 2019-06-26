package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.LibraryBranch;

public class BorrowService extends Service {
	
	private static BorrowService borrowService = null;
	
	private BorrowService () {
		getConnection();
	}
	
	public static BorrowService getInstance() {
		if(borrowService == null) {
			borrowService = new BorrowService();
		}
		return borrowService;
	}
	
	private Integer identityCheck() {
		
		Borrower borrower = new Borrower();
		BorrowerDAO bordao = new BorrowerDAO(conn);
		Integer cardNo = null;
		
		while (cardNo == null) {
			try {

				cardNo = UI.borrowerWelcome();

				borrower = bordao.selectBorrowerByID(cardNo);
				
				if(borrower == null) {
					return null;
				}

				System.out.println("Borrower:" + borrower.getBorrowerName());

			} catch (Exception e) {
				System.out.println("Invalid Card Number ");
				cardNo = null;
			}
		}
		return cardNo;
	}
	
	private boolean checkOutBook(Integer cardNo) throws ClassNotFoundException, SQLException {
		
		LibraryBranchDAO brdao = new LibraryBranchDAO(conn);
		BookLoansDAO bldao = new BookLoansDAO(conn);
		BookCopiesDAO bcdao = new BookCopiesDAO(conn);
	
		// select branch
		LibraryBranch lbEntity = UI.selectBranchFromList(brdao.readAll());
		// if quit operation
		if (lbEntity == null) {
			conn.rollback();
			return false;
		}
		// extract bookcopy list of that branch
		List<BookCopies> bookcopieslist = bcdao.readAllBookCopiesFromBranch(lbEntity.getLibraryBranchId());
		// select book
		BookCopies bookCopies = UI.uiShowGenericList(bookcopieslist, "book copy", true);
		// if quit operation
		if (bookCopies == null) {
			conn.rollback();
			return false;
		}
		// update bookcopy record
		bcdao.borrowReturnFunction("-",bookCopies.getBook().getBookId(), bookCopies.getBranch().getLibraryBranchId());
		// update bookloans
		bldao.borrowBook(new Object[] {bookCopies.getBook().getBookId(), bookCopies.getBranch().getLibraryBranchId(), cardNo});
		return true;
	}
	
	private boolean returnBook(Integer cardNo) throws ClassNotFoundException, SQLException {
		
		BookLoansDAO bldao = new BookLoansDAO(conn);
		BookCopiesDAO bcdao = new BookCopiesDAO(conn);
		
		// get book loan records list
		List<BookLoans> bookloansRecords = bldao.readBookLoansByID("CardNo", cardNo);
		if (bookloansRecords.size() == 0) {
			System.out.println("You don't have any borrowed book.");
			return false;
		}
		// get exact book loan record
		BookLoans bookloan = UI.uiShowGenericList(bookloansRecords, "loaned book record", true);
		
		// if quit operation
		if (bookloan == null) {
			conn.rollback();
			return false;
		}

		// updating
		// update book copy record
		bcdao.borrowReturnFunction("+", bookloan.getBook().getBookId(), bookloan.getBranch().getLibraryBranchId());
		// update bookLoan record
		bldao.update(bookloan, "dateIn");
		return true;
	}
	
	// Borrower function
	public void startService() {
		// connection
		getConnection();
		
		try {
			//check identity
			Integer cardNo = identityCheck();
			if(cardNo == null) {
				return;
			}

			Integer welChoice = null;
			do {

				// Borrower Choose Function
				welChoice = UI.borrowerChooseFunction();
				
				if (welChoice == 3) {
					break;
				}
				if (welChoice == 1) { // Check out a book

					if(!checkOutBook(cardNo)) {
						continue;
					}

				} else { // Return a book
					
					if(!returnBook(cardNo)) {
						continue;
					}
				}
				
				// commit the change
				conn.commit();
				
				System.out.println("Change saved");
				
			} while (welChoice != 3 || welChoice != 0);

		} catch (ClassNotFoundException e) {
			System.out.println("Connection Issue");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Invalid SQL");
			e.printStackTrace();

		} finally {
			closeConnection();
		}

	}

}
