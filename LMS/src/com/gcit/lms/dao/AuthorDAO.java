package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Author;


public class AuthorDAO extends BaseDAO<Author>{
	
	public AuthorDAO(Connection conn) {
		super(conn);
	}

	public Integer add(Author author) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_author (authorName) values (?)", 
				new Object[]{author.getAuthorName()}
		);
	}

	public void update(Author author, String field) throws ClassNotFoundException, SQLException {
		saveWithID(
				"update tbl_author set " + field + " = ? where authorId = ?", 
				new Object[]{author.getAuthorName(), author.getAuthorId()}
		);
	}

	public void delete(Author author) throws ClassNotFoundException, SQLException {
		//check if exist book
		
		if(author.getBooks().size() != 0) {
			System.out.println("The Author has coresponed book exist, do you still want to delete it?[Y/n]");
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		save(
				"delete from tbl_author where authorId = ?", 
				new Object[]{author.getAuthorId()}
		);
	}
	
	public List<Author> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_author", null);
	}
	
	public Author readAuthorByID(String pk) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_author where authorId = ?", new Object[] {pk}).get(0);
	}
	
	public List<Author> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Author> authors = new ArrayList<>();
		BookDAO bdao = new BookDAO(conn);
		while(rs.next()){
			Author author = new Author();
			
			author.setAuthorId(rs.getInt("authorId"));
			author.setAuthorName(rs.getString("authorName"));
			
			//populate my books
			author.setBooks(
					bdao.readFirstLevel(
							"select * from tbl_book where bookId IN ( select bookId from tbl_book_authors where authorId = ?)", 
							new Object[]{
									author.getAuthorId()
							}
					)
			);
			
			//add to the list
			authors.add(author);
		}
		return authors;
	}
	
	public List<Author> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Author> authors = new ArrayList<>();
		while(rs.next()){
			Author author = new Author();
			
			author.setAuthorId(rs.getInt("authorId"));
			author.setAuthorName(rs.getString("authorName"));
			
			authors.add(author);
		}
		return authors;
	}
}
