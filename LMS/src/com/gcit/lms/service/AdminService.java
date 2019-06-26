package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BaseDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Entity;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.LibraryBranch;
import com.gcit.lms.entity.Publisher;

public class AdminService extends Service {

	private static AdminService adminInstance = null;

	private AdminService() {
		getConnection();
	}

	public static AdminService getInstance() {
		if (adminInstance == null) {
			adminInstance = new AdminService();
		}
		return adminInstance;
	}

	private boolean extendDueDate() throws ClassNotFoundException, SQLException {

		do {
			// show list of borrower to choose from
			BorrowerDAO bordao = new BorrowerDAO(conn);
			Borrower borrower = UI.uiShowGenericList(bordao.readAll(), "borrower", true);
			if (borrower == null) {
				break;
			}
			// for same user:
			do {
				// show list of borrowed book
				BookLoansDAO bldao = new BookLoansDAO(conn);
				List<BookLoans> bookloansRecord = bldao.readBookLoansByID("CardNo", borrower.getCardId());

				if (bookloansRecord.size() == 0) {
					System.out.println("No such record exist!");
					break;
				}
				BookLoans bookloans = UI.uiShowGenericList(bookloansRecord, "book loan record", true);
				if (bookloans == null) {
					// quit
					break;
				}
				// update the due date
				bldao.update(bookloans, "dueDate");
				conn.commit();

			} while (true);

		} while (true);
		return true;
	}

	private Integer setPublisher(Book book) throws ClassNotFoundException, SQLException {

		BookDAO BookDAO = new BookDAO(conn);
		PublisherDAO pdao = new PublisherDAO(conn);
		int choice;
		while (true) {
			// ask choice
			choice = UI.AdminAddChoice("publisher");
			// Quitting
			if (choice > 2) {
				if (choice == 3) {
					System.out.println("You have to define at least one publisher");
					continue;
				} else {
					return null;
					// break;
				}
			}

			// dealing w/ Publisher
			if (choice == 1) {// choose from exist
				Publisher publisher = UI.uiShowGenericList(pdao.readAll(), "publisher", true);
				if (publisher == null) {
					return null;
				}
				book.setPublisher(publisher);

			} else {// create new
				Publisher pe = new Publisher();
				pe.setPublisherName(UI.specificInput("publisher name", true));
				pe.setPublisherAddress(UI.specificInput("publisher address", true));
				pe.setPublisherPhone(UI.specificInput("publisher phone", true));
				pe.setPublisherId(pdao.add(pe));
				book.setPublisher(pe);
			}
			return BookDAO.add(book);

		}
	}

	private boolean setAuthor(Integer bookId) throws ClassNotFoundException, SQLException {

		BookDAO BookDAO = new BookDAO(conn);
		AuthorDAO adao = new AuthorDAO(conn);

		int authorcount = 0;
		int choice;
		do {
			choice = UI.AdminAddChoice("author");
			if (choice == 3) {
				if (authorcount == 0) {
					System.out.println("You have to define at least one author");
					continue;
				} else {
					// cancel operation
					return true;
					// break;
				}
			} else if (choice == 1) {
				Author author = UI.uiShowGenericList(adao.readAll(), "author", true);
				BookDAO.addBookAuthors(bookId, author.getAuthorId());
			} else if (choice == 2) {
				Author ae = new Author();
				ae.setAuthorName(UI.specificInput("author name", true));
				Integer authorId = adao.add(ae);
				BookDAO.addBookAuthors(bookId, authorId);
			}
			authorcount++;
		} while (choice != 4);
		return false;
	}

	private boolean setGenre(Integer bookId) throws ClassNotFoundException, SQLException {

		BookDAO BookDAO = new BookDAO(conn);
		GenreDAO gdao = new GenreDAO(conn);

		int genrecount = 0;
		int choice;
		do {
			choice = UI.AdminAddChoice("genre");
			if (choice == 3) {
				if (genrecount == 0) {
					System.out.println("You have to define at least one genre");
					continue;
				} else {
					return true;
				}
			} else if (choice == 1) {
				Genre genre = UI.uiShowGenericList(gdao.readAll(), "genre", true);
				BookDAO.addBookGenres(bookId, genre.getGenreId());
			} else if (choice == 2) {
				Genre ge = new Genre();
				ge.setGenreName(UI.specificInput("genre name", true));
				Integer genreId = gdao.add(ge);
				BookDAO.addBookGenres(bookId, genreId);
			}
			genrecount++;
		} while (choice != 4);
		return false;
	}

	private boolean setBranch(Integer bookId) throws ClassNotFoundException, SQLException {
		// dealing w/ library branch and bookCopies

		BookDAO BookDAO = new BookDAO(conn);
		LibraryBranchDAO lbdao = new LibraryBranchDAO(conn);

		int LibraryBranchcount = 0;
		int choice;
		do {
			choice = UI.AdminAddChoice("library branch");
			if (choice == 3) {
				if (LibraryBranchcount == 0) {
					System.out.println("You have to define at least one library branch");
				} else {
					return true;
				}
			} else if (choice == 1) {
				LibraryBranch libraryBranch = UI.uiShowGenericList(lbdao.readAll(), "LibraryBranch", true);
				String noOfCopies = UI.specificInput("number of copies", true);
				BookDAO.addBookLibraryBranchs(bookId, libraryBranch.getLibraryBranchId(), noOfCopies);
			} else if (choice == 2) {
				LibraryBranch libraryBranch = new LibraryBranch();
				libraryBranch.setLibraryBranchName(UI.specificInput("Library Branch name", true));
				libraryBranch.setLibraryBranchAddress(UI.specificInput("Library branch address", true));
				Integer LibraryBranchId = lbdao.add(libraryBranch);
				String noOfCopies = UI.specificInput("number of copies", true);
				BookDAO.addBookLibraryBranchs(bookId, LibraryBranchId, noOfCopies);
			}
			LibraryBranchcount++;
		} while (choice != 4);
		return false;
	}

	private boolean adminAddBook() throws SQLException, ClassNotFoundException {
		Book book = new Book();
		// dealing with book name;
		book.setTitle(UI.specificInput("Book name", true));
		if (book.getTitle() == null) {
			return false;
		}

		// dealing w/ publisher, add book, retrieve a temporary book primary key
		Integer bookId = setPublisher(book);
		if (bookId == null) {
			conn.rollback();
			return false;
		}

		// dealing w/ author
		if (!setAuthor(bookId)) {
			conn.rollback();
			return false;
		}

		// dealing w/ genre
		if (!setGenre(bookId)) {
			conn.rollback();
			return false;
		}

		// dealing w/ branch
		if (!setBranch(bookId)) {
			conn.rollback();
			return false;
		}

		conn.commit();
		return true;
	}

	private boolean changePublisher(Book book) throws ClassNotFoundException, SQLException {

		BookDAO bookDAO = new BookDAO(conn);
		PublisherDAO pdao = new PublisherDAO(conn);
		int choice;

		choice = UI.AdminUpdateChoice("publisher");
		if (choice == 3) {
			return true;
		}
		if (choice == 1) {// select from exist

			Publisher publisher = UI.uiShowGenericList(pdao.readAll(), "publisher", true);
			book.setPublisher(publisher);
			bookDAO.update(book, "pubId");

		} else if (choice == 2) {// create a new one

			Publisher pe = new Publisher();

			// get basic info
			pe.setPublisherName(UI.specificInput("publisher name", true));
			pe.setPublisherAddress(UI.specificInput("publisher address", true));
			pe.setPublisherPhone(UI.specificInput("publisher phone", true));

			// get new pubId
			// set book entity
			pe.setPublisherId(pdao.add(pe));
			book.setPublisher(pe);

			// update
			bookDAO.update(book, "pubId");

		} else { // choice == 4
			return false;
		}
		return true;
	}

	private boolean changeAuthor(Book book) throws ClassNotFoundException, SQLException {

		BookDAO bookDAO = new BookDAO(conn);
		AuthorDAO adao = new AuthorDAO(conn);
		int choice;
		do {
			choice = UI.adminChooseIfChangeORAdd("author");
			book = bookDAO.readBookWithID("bookId", book.getBookId().toString()).get(0);
			if (choice > 3) {
				break;
			}
			if (choice == 3) {// Change from exist
				// choose the one to be changed
				Author oldauthor = UI.uiShowGenericList(book.getAuthors(), "author", true);
				if (oldauthor == null) {

					continue;
				}
			
				// choose the new one
				Author newauthor = UI.uiShowGenericList(adao.readAll(), "author", true);
				if (newauthor == null) {

					continue;
				}

				// update by delete the old one and insert a new one
				bookDAO.deleteBookAuthor(book.getBookId(), oldauthor.getAuthorId());
				bookDAO.addBookAuthors(book.getBookId(), newauthor.getAuthorId());
//				

			} else if (choice == 1) {// add

				Integer c = UI.AdminUpdateChoice("author");

				if (c == 2) {// add w/ new author
					// create new author
					Author ge = new Author();
					ge.setAuthorName(UI.specificInput("author", true));
					// add author to author table
					Integer authorId = adao.add(ge);
					if (authorId == null) {

						continue;
					}
					// link up book and new author
					bookDAO.addBookAuthors(book.getBookId(), authorId);

				} else if (c == 1) { // add w/ existed
					// selecting from exist
					Author author = UI.uiShowGenericList(adao.readAll(), "author", true);
					if (author == null) {

						continue;
					}
					System.out.println("id:"+author.getAuthorId()+" Name: "+author.getAuthorName());
					// link up book and author
					bookDAO.addBookAuthors(book.getBookId(), author.getAuthorId());

				} else if (c == 3) {// proceed to next attribute

					break;
				} else { // quit to menu
					choice = 5;
					break;
				}

			} else {// delete

				Author author = UI.uiShowGenericList(book.getAuthors(), "author", true);
				if (author == null) {
					break;
				}
				bookDAO.deleteBookAuthor(book.getBookId(), author.getAuthorId());
			}
		} while (true);
		if (choice == 5) {
			return false;
		}
		return true;
	}

	private boolean changeGenre(Book book) throws ClassNotFoundException, SQLException {

		BookDAO bookDAO = new BookDAO(conn);
		GenreDAO gdao = new GenreDAO(conn);

		int choice;
		// change Book Genre
		do {
			book = bookDAO.readBookWithID("bookId", book.getBookId().toString()).get(0);
			choice = UI.adminChooseIfChangeORAdd("genre");
			if (choice > 3) {
				break;
			}
			if (choice == 3) {// Change the exist one

				// get old record
				Genre oldgenre = UI.uiShowGenericList(book.getGenres(), "genre", true);
				if (oldgenre == null) {
					continue;
				}
				// get new record
				Genre newgenre = UI.uiShowGenericList(gdao.readAll(), "genre", true);
				if (newgenre == null) {
					continue;
				}
				// update
				bookDAO.deleteBookGenre(book.getBookId(), oldgenre.getGenreId());
				bookDAO.addBookGenres(book.getBookId(), newgenre.getGenreId());

			} else if (choice == 1) {// add new one
				Integer c = UI.AdminUpdateChoice("genre");
				if (c == 2) {// add w/ new
					Genre ge = new Genre();
					ge.setGenreName(UI.specificInput("genre", true));
					Integer genreId = gdao.add(ge);
					bookDAO.addBookGenres(book.getBookId(), genreId);

				} else if (c == 1) { // add w/ existed

					Genre genre = UI.uiShowGenericList(gdao.readAll(), "genre", true);
					bookDAO.addBookGenres(book.getBookId(), genre.getGenreId());

				} else if (c == 3) {// proceed

					break;
				} else { // quit
					return false;
				}

			} else { // delete

				Genre genre = UI.uiShowGenericList(book.getGenres(), "genre", true);
				bookDAO.deleteBookGenre(book.getBookId(), genre.getGenreId());
			}
		} while (true);
		if (choice == 5) {
			return false;
		}
		return true;
	}

	private boolean changeBranch(Book book) throws ClassNotFoundException, SQLException {

		BookDAO bookDAO = new BookDAO(conn);
		LibraryBranchDAO lbdao = new LibraryBranchDAO(conn);
		BookCopiesDAO bcdao = new BookCopiesDAO(conn);

		int choice;
		do {
			book = bookDAO.readBookWithID("bookId", book.getBookId().toString()).get(0);
			choice = UI.AdminUpdateChoice("library branch");
			if (choice > 2) {
				break;
			} else if (choice == 1) {// select from exist

				// select a branch
				LibraryBranch libraryBranch = UI.uiShowGenericList(lbdao.readLibraryBranchExcludsive(book),
						"library branch", true);
				// if quit
				if (libraryBranch == null) {
					continue;
				}
				// ask for no of copies
				String noOfCopies = UI.specificInput("number of copies", true);
				if (noOfCopies == null) {
					continue;
				}
				// update book copies table
				BookCopies be = new BookCopies();
				LibraryBranch lbentity = new LibraryBranch();
				lbentity.setLibraryBranchId(libraryBranch.getLibraryBranchId());
				be.setNoOfCopies(Integer.parseInt(noOfCopies));
				be.setBook(book);
				be.setBranch(lbentity);
				bcdao.add(be);

//				bcdao.Update(be, "noOfCopies", Bookpk+LibraryBranchId);

			} else {// create a new branch
				LibraryBranch lbe = new LibraryBranch();
				// acquiring info of branch
				String lbName = UI.specificInput("library branch name", true);
				if (lbName == null) {
					continue;
				}
				String lbAddress = UI.specificInput("library branch address", true);
				if (lbAddress == null) {
					continue;
				}
				lbe.setLibraryBranchAddress(lbName);
				lbe.setLibraryBranchName(lbAddress);
				// add new branch
				Integer LibraryBranchId = lbdao.add(lbe);
				// ask for no of copies
				String noOfCopies = UI.specificInput("number of copies", true);
				// update book copy table
				bookDAO.addBookLibraryBranchs(book.getBookId(), LibraryBranchId, noOfCopies);
			}

		} while (true);
		if (choice == 4) {
			return false;
		}

		return true;
	}

	private boolean adminUpdateBook() throws ClassNotFoundException, SQLException {
		
		BookDAO bookDAO = new BookDAO(conn);

		// Choose an existing book
		Book book = UI.uiShowGenericList(bookDAO.readAll(), "Book", true);
		if (book == null) {
			return false;
		}
		int choice = UI.adminBookUpdateChoice();
		switch (choice) {
		case 1: // book name
			// change Book name
			String bookName = UI.specificInput("book name", false);
			if (bookName != null) {
				book.setTitle(bookName);
				bookDAO.update(book, "title");
			}
			break;

		case 5: // book publisher
			// change Book publisher
			if (!changePublisher(book)) {
				conn.rollback();
				return false;
			}
			break;

		case 2: // author
			if (!changeAuthor(book)) {
				conn.rollback();
				return false;
			}
			break;
		case 3: // genre
			if (!changeGenre(book)) {
				conn.rollback();
				return false;
			}
			break;
		case 4: // library branch
			if (!changeBranch(book)) {
				conn.rollback();
				return false;
			}
			break;
		}

		conn.commit();
		return true;
	}

	private boolean changeBook(Integer funChoice) throws ClassNotFoundException, SQLException {

		BookDAO bookDAO = new BookDAO(conn);

		switch (funChoice) {
		case 1:// read

			UI.uiShowGenericList(bookDAO.readAll(), "book", false);
			break;

		case 2:// delete

			Book book = UI.uiShowGenericList(bookDAO.readAll(), "book", true);
			if (book == null) {
				return false;
			}
			bookDAO.delete(book);
			conn.commit();
			break;

		case 3:// add

			if (!adminAddBook()) {
				return false;
			}

			break;
		case 4:// update
			if (!adminUpdateBook()) {
				return false;
			}

			break;
		}
		return true;
	}

	private boolean nonRelationalModify(Integer tblChoice, Integer funChoice)
			throws ClassNotFoundException, SQLException {

		String field = null;
		BaseDAO DAO = null;

		// pre-dealing with table
		switch (tblChoice) {

		case 1:
			field = "author";
			DAO = new AuthorDAO(conn);
			break;

		case 2:
			field = "borrower";
			DAO = new BorrowerDAO(conn);
			break;

		case 3:
			field = "genre";
			DAO = new GenreDAO(conn);
			break;

		case 4:
			field = "publisher";
			DAO = new PublisherDAO(conn);
			break;

		}

		// perform function accordingly
		switch (funChoice) {

		case 1: // Read

			UI.uiShowGenericList(DAO.readAll(), field, false);
			break;

		case 2: // delete

			Entity e = UI.uiShowGenericList(DAO.readAll(), field, true);
			if (e == null) {
				return false;
			}
			DAO.delete(e);
			break;

		case 3: // add

			switch (tblChoice) {

			case 1: // add author

				Author author = new Author();
				author.setAuthorName(UI.specificInput("author name", true));
				nonRelationalAdd(DAO, author);
				break;

			case 2: // add borrower

				Borrower borrower = new Borrower();
				borrower.setBorrowerName(UI.specificInput("borrower name", true));
				borrower.setBorrowerAddress(UI.specificInput("borrower address", true));
				borrower.setBorrowerPhone(UI.specificInput("borrower Phone", true));
				nonRelationalAdd(DAO, borrower);
				break;

			case 3:// add genre

				Genre genre = new Genre();
				genre.setGenreName(UI.specificInput("genre name", true));
				nonRelationalAdd(DAO, genre);
				break;

			case 4: // add publisher

				Publisher publisher = new Publisher();
				publisher.setPublisherName(UI.specificInput("publisher name", true));
				publisher.setPublisherAddress(UI.specificInput("publisher address", true));
				publisher.setPublisherPhone(UI.specificInput("publisher phone", true));
				nonRelationalAdd(DAO, publisher);
				break;

			default:
				System.out.println("Somethings wrong");
				break;
			}

			break;

		case 4:// update

			switch (tblChoice) {

			case 1: // author

				Author author = (Author) UI.uiShowGenericList(DAO.readAll(), "author", true);
				if (author == null) {
					return false;
				}
				// change author name
				String authorName = UI.specificInput("author name", false);
				author.setAuthorName(authorName);
				nonRelationalUpdate(author, DAO, "authorName", authorName);
				break;

			case 2: // borrower

				Borrower borrower = (Borrower) UI.uiShowGenericList(DAO.readAll(), "Borrower", true);
				if (borrower == null) {
					return false;
				}
				// change Borrower name
				String BorrowerName = UI.specificInput("Borrower name", false);
				borrower.setBorrowerName(BorrowerName);
				nonRelationalUpdate(borrower, DAO, "name", BorrowerName);

				// change Borrower address
				String BorrowerAddress = UI.specificInput("Borrower address", false);
				borrower.setBorrowerAddress(BorrowerAddress);
				nonRelationalUpdate(borrower, DAO, "address", BorrowerAddress);

				// change Borrower phone
				String Borrowerphone = UI.specificInput("borrower phone", false);
				borrower.setBorrowerPhone(Borrowerphone);
				nonRelationalUpdate(borrower, DAO, "phone", Borrowerphone);
				break;

			case 3: // genre

				Genre genre = (Genre) UI.uiShowGenericList(DAO.readAll(), "genre", true);
				if (genre == null) {
					return false;
				}
				// change genre name
				String genreName = UI.specificInput("genre name", false);
				genre.setGenreName(genreName);
				nonRelationalUpdate(genre, DAO, "genreName", genreName);
				break;

			case 4: // publisher

				Publisher publisher = (Publisher) UI.uiShowGenericList(DAO.readAll(), "Publisher", true);
				if (publisher == null) {
					return false;
				}
				// change Publisher name
				String PublisherName = UI.specificInput("Publisher name", false);
				publisher.setPublisherName(PublisherName);
				nonRelationalUpdate(publisher, DAO, "publisherName", PublisherName);

				// change Publisher address
				String PublisherAddress = UI.specificInput("Publisher address", false);
				publisher.setPublisherAddress(PublisherAddress);
				nonRelationalUpdate(publisher, DAO, "publisherAddress", PublisherAddress);

				// change Publisher phone
				String Publisherphone = UI.specificInput("Publisher phone", false);
				publisher.setPublisherPhone(Publisherphone);
				nonRelationalUpdate(publisher, DAO, "publisherPhone", PublisherAddress);
				break;

			default:
				System.out.println("Somethings wrong");
				break;
			}

			break;
		}

		return true;
	}

	private boolean libraryAdmin(Integer tblChoice, Integer funChoice) throws ClassNotFoundException, SQLException {
		String field = "branch";
		LibraryBranchDAO DAO = new LibraryBranchDAO(conn);

		switch (funChoice) {

		case 1: // Read
			BookCopiesDAO bcdao = new BookCopiesDAO(conn);
			while(true) {
				// select branch from list
				LibraryBranch lb = UI.uiShowGenericList(DAO.readAll(), field, true);
				if(lb == null) {
					break;
				}
				// select book copies for this branch;
				UI.uiShowGenericList(bcdao.readAllBookCopiesFromBranch(lb.getLibraryBranchId()), "book copies", false);
			}

			break;

		case 2: // delete

			LibraryBranch e = UI.uiShowGenericList(DAO.readAll(), field, true);
			if (e == null) {
				return false;
			}
			DAO.delete(e);
			break;

		case 3: // add

			LibraryBranch libraryBranch = new LibraryBranch();
			libraryBranch.setLibraryBranchName(UI.specificInput("library branch name", true));
			libraryBranch.setLibraryBranchAddress(UI.specificInput("library branch address", true));
			nonRelationalAdd(DAO, libraryBranch);
			break;

		case 4: // update

			LibraryBranch libraryBranch2 = (LibraryBranch) UI.uiShowGenericList(DAO.readAll(), "Library Branch", true);
			if (libraryBranch2 == null) {
				return false;
			}
			// change LibraryBranch name
			String LibraryBranchName = UI.specificInput("Library Branch name", false);
			libraryBranch2.setLibraryBranchName(LibraryBranchName);
			nonRelationalUpdate(libraryBranch2, DAO, "branchName", LibraryBranchName);

			// change LibraryBranch address
			String LibraryBranchAddress = UI.specificInput("LibraryBranch address", false);
			libraryBranch2.setLibraryBranchAddress(LibraryBranchAddress);
			nonRelationalUpdate(libraryBranch2, DAO, "branchAddress", LibraryBranchAddress);
			break;
		}
		return true;
	}

	public void startService() {

		do {
			try {
				// connection
				getConnection();

				// get user function choice
				Integer tblChoice = UI.adminWelcome();
				Integer funChoice = null;
				if (tblChoice == 8) {
					return;
				}
				do {

					if (tblChoice == 7) { // Extend due date

						if (extendDueDate()) {
							break;
						}

					} else if (tblChoice == 6) { // Change to book

						funChoice = UI.adminOperations();

						// if quit
						if (funChoice == 5) {
							break;
						}

						if (!changeBook(funChoice)) {
							continue;
						}

					} else if (tblChoice == 5) {

						funChoice = UI.adminOperations();

						// if quit
						if (funChoice == 5) {
							break;
						}

						if (libraryAdmin(tblChoice, funChoice)) {
							continue;
						}

					} else { // non relational

						funChoice = UI.adminOperations();

						// if quit
						if (funChoice == 5) {
							break;
						}
						if (!nonRelationalModify(tblChoice, funChoice)) {
							continue;
						}
					}
					conn.commit();
				} while (true);

			} catch (ClassNotFoundException e) {
				System.out.println("Connection Issue");

			} catch (SQLException e) {
				System.out.println("Invalid SQL");
				e.printStackTrace();

			} finally {
				closeConnection();
			}

		} while (true);
	}
}
