/**
 * 
 */
package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Publisher;

public class PublisherDAO extends BaseDAO<Publisher>{

	public PublisherDAO(Connection connection) {
		super(connection);
	}

	public Integer add(Publisher publisher) throws ClassNotFoundException, SQLException {
		return saveWithID(
				"insert into tbl_publisher (publisherName, publisherAddress, publisherPhone) values (?,?,?)", 
				new Object[]{
						publisher.getPublisherName(),
						publisher.getPublisherAddress(),
						publisher.getPublisherPhone()
				}
		);
	}

	public void update(Publisher publisher,String field) throws ClassNotFoundException, SQLException {
		Object field1 = null;
		if(field.contentEquals("publisherName")) {
			field1 = publisher.getPublisherName();
		}
		if(field.contentEquals("publisherAddress")) {
			field1 = publisher.getPublisherAddress();
		}
		if(field.contentEquals("publisherPhone")) {
			field1 = publisher.getPublisherPhone();
		}
		saveWithID(
				"update tbl_publisher set " + field + "= ? where publisherId = ?", 
				new Object[]{field1, publisher.getPublisherId()}
		);
	}

	public void delete(Publisher publisher) throws ClassNotFoundException, SQLException {
		//check if exist book
		
		if(publisher.getBooks().size() != 0) {
			System.out.println("The publisher has coresponed book exist, do you still want to delete it?[Y/n]");
			String input = in.nextLine();
			if(input.toUpperCase().contentEquals("N")) {
				return;
			}
		}
		saveWithID(
				"delete from tbl_publisher where publisherId = ?", 
				new Object[]{ publisher.getPublisherId()}
		);
	}
	
	public List<Publisher> readAll() throws ClassNotFoundException, SQLException {
		return read("select * from tbl_publisher", null);
	}
	
	public Publisher readPublisherByID(String pk) throws ClassNotFoundException, SQLException {
		return read("select * from tbl_publisher where publisherId = ?", new Object[] {pk}).get(0);
	}
	
	public List<Publisher> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Publisher> publishers = new ArrayList<>();
		BookDAO bdao = new BookDAO(conn);
		while(rs.next()){
			Publisher publisher = new Publisher();
			publisher.setPublisherId(rs.getInt("publisherId"));
			publisher.setPublisherName(rs.getString("publisherName"));
			publisher.setPublisherAddress(rs.getString("publisherAddress"));
			publisher.setPublisherPhone(rs.getString("publisherPhone"));
			//populate my books
			publisher.setBooks(bdao.readFirstLevel("select * from tbl_book where pubId = ?", new Object[]{publisher.getPublisherId()}));
			publishers.add(publisher);
		}
		return publishers;
	}
	
	public List<Publisher> extractDataFirstLevel(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Publisher> publishers = new ArrayList<>();
		while(rs.next()){
			Publisher publisher = new Publisher();
			publisher.setPublisherId(rs.getInt("publisherId"));
			publisher.setPublisherName(rs.getString("publisherName"));
			publisher.setPublisherAddress(rs.getString("publisherAddress"));
			publisher.setPublisherPhone("publisherPhone");
			publishers.add(publisher);
		}
		return publishers;
	}
}
