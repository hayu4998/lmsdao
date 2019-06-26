package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Genre;

public class GenreDAO extends BaseDAO<Genre>{
	
	public GenreDAO(Connection conn) {
		super(conn);
	}

	public Integer add(Genre genre) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_genre (genre_name) values (?)", 
				new Object[]{genre.getGenreName()}
		);
	}

	public void update(Genre genre, String field) throws ClassNotFoundException, SQLException {
		save(
				"update tbl_genre set genre_name = ? where genre_id = ?", 
				new Object[]{genre.getGenreName(), genre.getGenreId()}
		);
	}

	public void delete(Genre genre) throws ClassNotFoundException, SQLException {
		
		
		if(genre.getBooks().size() != 0) {
			System.out.println("The genre has coresponed book exist, do you still want to delete it?[Y/n]");
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		
		save(
				"delete from tbl_genre where genre_id = ?", 
				new Object[]{genre.getGenreId()}
		);
	}
	
	public List<Genre> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_genre", null);
	}
	
	public Genre readGenreByID(String pk) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_genre where genre_id = ?", new Object[] {pk}).get(0);
	}
	
	public List<Genre> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Genre> genres = new ArrayList<>();
		BookDAO bdao = new BookDAO(conn);
		while(rs.next()){
			Genre genre = new Genre();
			
			genre.setGenreId(rs.getInt("genre_id"));
			genre.setGenreName(rs.getString("genre_name"));
			
			//populate book list
			genre.setBooks(
					bdao.readFirstLevel(
							"select * from tbl_book where bookId IN ( select bookId from tbl_book_genres where genre_id = ?)", 
							new Object[]{genre.getGenreId()}
					)
			);
			
			//add to the list
			genres.add(genre);
		}
		return genres;
	}
	
	public List<Genre> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Genre> genres = new ArrayList<>();
		while(rs.next()){
			Genre genre = new Genre();
			
			genre.setGenreId(rs.getInt("genre_id"));
			genre.setGenreName(rs.getString("genre_name"));
			
			genres.add(genre);
		}
		return genres;
	}
}
