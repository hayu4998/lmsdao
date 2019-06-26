package com.gcit.lms.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class BookLoans extends Entity{
	private Book book;
	private LibraryBranch branch;
	private Borrower borrower;
	private String loanedDateOut;
	private String loanedDateIn;
	private String loanedDueDate;
	
	@Override
	public String getGenericInfo() {
		return (
				"Loaner: " + borrower.getBorrowerName()
				+", Title: " + book.getTitle()
				+", Branch: " +branch.getLibraryBranchName()
				+", DueDate: " + loanedDueDate
				);
	}
	
	
	
	
	/**
	 * @return the book
	 */
	public Book getBook() {
		return book;
	}
	/**
	 * @param book the book to set
	 */
	public void setBook(Book book) {
		this.book = book;
	}
	/**
	 * @return the branch
	 */
	public LibraryBranch getBranch() {
		return branch;
	}
	/**
	 * @param branch the branch to set
	 */
	public void setBranch(LibraryBranch branch) {
		this.branch = branch;
	}
	/**
	 * @return the borrower
	 */
	public Borrower getBorrower() {
		return borrower;
	}
	/**
	 * @param borrower the borrower to set
	 */
	public void setBorrower(Borrower borrower) {
		this.borrower = borrower;
	}
	/**
	 * @return the loanedDateOut
	 */
	public String getLoanedDateOut() {
		return loanedDateOut.substring(0,19);
	}
	/**
	 * @param loanedDateOut the loanedDateOut to set
	 */
	public void setLoanedDateOut(String loanedDateOut) {
		this.loanedDateOut = loanedDateOut;
	}
	/**
	 * @return the loanedDateIn
	 */
	public String getLoanedDateIn() {
		return loanedDateIn;
	}
	/**
	 * @param loanedDateIn the loanedDateIn to set
	 */
	public void setLoanedDateIn(String loanedDateIn) {
		this.loanedDateIn = loanedDateIn;
	}
	/**
	 * @return the loanedDueDate
	 */
	public String getLoanedDueDate() {
		return loanedDueDate;
	}
	/**
	 * @param loanedDueDate the loanedDueDate to set
	 */
	public void setLoanedDueDate(String loanedDueDate) {
		this.loanedDueDate = loanedDueDate;
	}
	
	public Timestamp getNOW() {
		Date d = new Date();
		long time = d.getTime();
		Timestamp t = new Timestamp(time);
		return t;
	}
	
	public Timestamp getSevenDayLater() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 7);
		long time = cal.getTimeInMillis();
		Timestamp t = new Timestamp(time);
		return t;
	}
}
