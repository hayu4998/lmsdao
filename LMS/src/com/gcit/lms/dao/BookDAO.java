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


public class BookDAO extends BaseDAO<Book> {
	
	
	public BookDAO(Connection conn) {
		super(conn);
	}

	public Integer add(Book book) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_book (title,pubId) values (?,?)", 
				new Object[] { book.getTitle(), book.getPublisher().getPublisherId() }
			);
	}
	
//	public Integer addBookGetPK(Book book) throws ClassNotFoundException, SQLException {
//		return saveWithID(
//				"insert into tbl_book (title) values (?)", 
//				new Object[] { book.getTitle() }
//		);
//	}
	
	public void addBookAuthors(Integer bookId, Integer authorId)  throws ClassNotFoundException, SQLException {
		save(
				"insert into tbl_book_authors(bookId,authorId) values (?, ?)", 
				new Object[] { 
						bookId, 
						authorId
				}
			);
	}
	
	public void deleteBookAuthor(Integer bookId, Integer authorId) throws ClassNotFoundException, SQLException {
		save(
				"delete from tbl_book_authors where bookId = ? and authorId = ?", 
				new Object[] { 
						bookId,
						authorId
				}
		);
	}
	
	public void addBookGenres(Integer bookId, Integer genresId)  throws ClassNotFoundException, SQLException {
		save(
				"insert into tbl_book_genres(bookId, genre_id) values (?, ?)", 
				new Object[] { 
						bookId, 
						genresId
				}
		);
	}
	
	public void deleteBookGenre(Integer bookId, Integer genresId) throws ClassNotFoundException, SQLException {
		save(
				"delete from tbl_book_genres where bookId = ? and genre_id = ?", 
				new Object[] { 
						bookId,
						genresId
				}
		);
	}
	
	
	
	public void addBookLibraryBranchs(Integer newBookPK, Integer libraryBranchId, String noOfCopies) throws ClassNotFoundException, SQLException {
		saveWithID(
				"insert into tbl_book_copies(bookId, branchId, noOfCopies) values (?, ?, ?)", 
				new Object[] { 
						newBookPK, 
						libraryBranchId,
						noOfCopies
				}
		);
	}

	public void update(Book book, String field) throws ClassNotFoundException, SQLException {
		
		Object field1 = null;

			if(field.contentEquals("title")) {
				
				field1 = book.getTitle();
				
			}else if(field.contentEquals("pubId")){
				
				field1 = book.getPublisher().getPublisherId();
				
			}
		
		save("update tbl_book set " + field + " = ? where bookId = ?", new Object[] { field1, book.getBookId() });
	}

	public void delete(Book book) throws ClassNotFoundException, SQLException {
		
		//check if exist book loan records
		BookLoansDAO bldao = new BookLoansDAO(conn);
		if(bldao.readBookLoansByID("bookId",book.getBookId()).size() != 0) {
			System.out.println("The Book has borrowing record, do you still want to delete it?[Y/n]");
			
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		
		saveWithID(
				"delete from tbl_book where bookId = ?", 
				new Object[] { book.getBookId() }
		);
	}

	public List<Book> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_book", null);
	}
	
	public List<Book> readBookWithID(String field, String value) throws ClassNotFoundException, SQLException{
		return read("select * from tbl_book where "+ field + " = ?", new Object[] {value});
	}

	public List<Book> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Book> books = new ArrayList<>();
		
		AuthorDAO adao = new AuthorDAO(conn);
		GenreDAO gdao = new GenreDAO(conn);
		BookCopiesDAO bcdao = new BookCopiesDAO(conn);
		BookLoansDAO bldao = new BookLoansDAO(conn);
		PublisherDAO pdao = new PublisherDAO(conn);
		
		while (rs.next()) {
			Book book = new Book();
			
			book.setBookId(rs.getInt("bookId"));
			book.setTitle(rs.getString("title"));
			
			//author
			book.setAuthors(
					adao.readFirstLevel(
							"select * from tbl_author where authorId IN ( select authorId from tbl_book_authors where bookId = ?)", 
							new Object[] {book.getBookId()}
					)
			);
			
			//genre
			book.setGenres(
					gdao.readFirstLevel(
							"select * from tbl_genre where genre_id IN ( select genre_id from tbl_book_genres where bookId = ?)", 
							new Object[] {book.getBookId()}
					)
			);
			
			//bookCopies
			book.setBookCopies(
					bcdao.readFirstLevel(
							"select * from tbl_book_copies bc where bc.bookId = ?", 
							new Object[] {book.getBookId()}
					)
			);
			
			//bookLoans
			book.setBookLoans(
					bldao.readFirstLevel(
							"select * from tbl_book_loans bl where bl.bookId = ?", 
							new Object[] {book.getBookId()}
					)
			);
			
			//publisher
			try {
				book.setPublisher(
						pdao.readFirstLevel(
								"select * from tbl_publisher where publisherId = ?", 
								new Object[] {rs.getInt("pubId")}
								).get(0)
						);
			}catch(IndexOutOfBoundsException e) {
				//In case of null Publisher
			}
			
			//add to the list
			books.add(book);
		}
		return books;
	}
	
	public List<Book> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book book = new Book();
			
			book.setBookId(rs.getInt("bookId"));
			book.setTitle(rs.getString("title"));
			
			books.add(book);
		}
		return books;
	}
	
}
