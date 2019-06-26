/**
 * 
 */
package com.gcit.lms.entity;


public class BookCopies extends Entity{
	private Book book;
	private LibraryBranch branch;
	private Integer noOfCopies;
	

	@Override
	public String getGenericInfo() {
		return (
				"Title: "+ book.getTitle() + 
				", Branch: " + branch.getLibraryBranchName() + 
				", No. of Copies: " + noOfCopies
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
	 * @return the noOfCopies
	 */
	public Integer getNoOfCopies() {
		return noOfCopies;
	}

	/**
	 * @param noOfCopies the noOfCopies to set
	 */
	public void setNoOfCopies(Integer noOfCopies) {
		this.noOfCopies = noOfCopies;
	}
	

}
