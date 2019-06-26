package com.gcit.lms.ui;

import java.util.List;
import java.util.Scanner;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Entity;
import com.gcit.lms.entity.LibraryBranch;

public class TextUI {

	private Scanner in;

	private static TextUI textUI_Instance = null;

	private TextUI() {
		in = new Scanner(System.in);
	}

	public static TextUI getInstance() {
		if (textUI_Instance == null) {
			textUI_Instance = new TextUI();
		}
		return textUI_Instance;
	}

	public Integer uiShowMenu() {
		StringBuilder content = new StringBuilder("Welcome to the GCIT Library Management System. Which category of user are you");
		content.append("\n\t1)Librarian \n\t2)Administrator \n\t3)Borrower \n\t4)Quit");
		return inputWCheck(4, 1, content);
	}

	public Integer inputWCheck(Integer max, Integer min, StringBuilder content) {
		String choice = null;
		Integer transChoice = null;
		boolean check = false;
		do {
			System.out.println(content);

			do {
				choice = in.nextLine();
			} while (choice.isEmpty());

			// check quit
			if (choice.contentEquals("quit")) {
				return 0;
			}

			// check boundary value;
			transChoice = Integer.parseInt(choice);
			if (transChoice < min || transChoice > max) {
				System.out.println("Invalid Input!");
			} else {
				check = true;
			}

		} while (!check);

		return transChoice;
	}

	public <T extends Entity> T uiShowGenericList(List<T> dataEntityList, String content, Boolean expectInput) {
		StringBuilder s = new StringBuilder("");
		if (expectInput) {
			System.out.println(s.append("Please pick one ").append( content).append(" from list below:"));
		} else {
			System.out.println(s.append("Here's the list of ").append(content).append(": "));
		}

		int count = 1;
		for (T a : dataEntityList) {
			System.out.println(count + ") " + a.getGenericInfo());
			count++;
		}

		Integer choice = null;
		if (expectInput) {
			System.out.println(count + ") " + "Quit");
			choice = inputWCheck(count, 1, new StringBuilder("Enter Your Choice: "));
			if (choice == count || choice == 0) {
				return null;
			}
		} else {
			return null;
		}

		return dataEntityList.get(choice - 1);
	}

	public Integer librarianWelcome() {
		StringBuilder content = new StringBuilder("Select function from below:\n\t1)Enter Branch you manage \n\t2)Quite to previous ");

		return inputWCheck(2, 1, content);
	}

	public LibraryBranch selectBranchFromList(List<LibraryBranch> branchs) {
		return uiShowGenericList(branchs, "branch", true);
	}

	public Integer librarianChooseFunction() {
		StringBuilder content = new StringBuilder("Select function from below:\n\t1)Update the details of the Library \n")
				.append("\t2)Add copies of Book to the Branch \n\t3)Quit to previous");
		return inputWCheck(3, 1, content);
	}

	public String librarianUpdateBranch(String content) {
		System.out.println(new StringBuilder("Please enter new branch ").append(content).append(" or enter N/A for no change"));
		String input = in.nextLine();
		if (input.toUpperCase().contentEquals("N/A")) {
			return null;
		} else {
			return input;
		}
	}

	public Book ChooseBookFromList(List<Book> books) {
		return uiShowGenericList(books, "book", true);
	}

	public Integer librarianModifyBookCopies(Integer noOfCopies) {
		System.out.println("Existing number of copy: " + noOfCopies);
		System.out.print("Enter new Number of copy: ");
		return in.nextInt();
	}

	public Integer borrowerWelcome() {
		System.out.print("Please Key in your cardNo: ");
		return in.nextInt();
	}

	public Integer borrowerChooseFunction() {
		StringBuilder content = new StringBuilder("Choose A function from list below:\n\t1)Check out a book\n\t2)Return a Book\n\t3)Quit to Previous");

		return inputWCheck(3, 1, content);
	}

	public Integer adminOperations() {
		StringBuilder content = new StringBuilder("Select the operation that you want to perform:\n")
				.append("\t1)Read\n\t2)Delete\n\t3)Add\n\t4)Update\n\t5)Quit");
		return inputWCheck(5, 1, content);
	}

	public Integer adminWelcome() {
		StringBuilder content = new StringBuilder("Select information tab that you want to modify from below:\n")
				.append("\t1)Author\n\t2)Borrower\n\t3)Genre\n\t4)Publisher\n\t5)Library Branch\n\t6)Book\n")
				.append("\t7)Extend DeadLine\n\t8)Quit");
		return inputWCheck(8, 1, content);

	}

	public String specificInput(String s, Boolean isAdding) {
		if (isAdding) {
			System.out.print("Please type in " + s + ": ");
		} else {
			System.out.print("Please type in " + s + ", N/A for no change: ");
		}
		String input = null;
		do {
			input = in.nextLine();
		} while (input.isEmpty());
		if (input.toUpperCase().contentEquals("N/A") && !isAdding) {
			return null;
		}
		return input;
	}

	public Integer AdminAddChoice(String content) {
		StringBuilder s = new StringBuilder("Would you like to link book with:\n\t1)an existed ");
		s.append(content);
		s.append("\n\t2)a new ");
		s.append(content);
		s.append("\n\t3)To next attribute\n\t4)Quit Operation and exit");
		return inputWCheck(4, 1, s);
	}

	public Integer AdminUpdateChoice(String content) {
		StringBuilder s = new StringBuilder("Would you like to link book with:");
		s.append("\n\t1)an existed ").append(content);
		s.append("\n\t2)a new ").append(content);
		s.append("\n\t3)proceed to next attribute\n\t4)Quit operation and exist");
		return inputWCheck(4, 1, s);
	}

	public String adminAskIfChange(String content) {
		StringBuilder s = new StringBuilder("Would you like to Change: ");
		s.append(content);
		s.append("[Y/n]");
		System.out.println(s);
		String input = in.nextLine();
		if (input.toUpperCase().contentEquals("N")) {
			return null;
		} else {
			System.out.println("Please type in new " + content + ": ");
			return in.nextLine();
		}
	}

	public Integer adminChooseIfChangeORAdd(String content) {
		StringBuilder s = new StringBuilder("Would you like to :");
		s.append("\n\t1) Add a ").append(content );
		s.append("\n\t2) Delete a ").append(content);
		s.append("\n\t3) Change a ").append(content);
		s.append("\n\t4) Proceed to the follower attribute\n\t5) Quit");
		return inputWCheck(5, 1, s);
	}
	
	public Integer adminBookUpdateChoice() {
		StringBuilder s = new StringBuilder("Choose one of the following tab to update:")
				.append("\n\t1)Name")
				.append("\n\t2)Author")
				.append("\n\t3)Genre")
				.append("\n\t4)LibraryBranch")
				.append("\n\t5)Publisher")
				.append("\n\t6)Quit to previous");
		
		return inputWCheck(6,1,s);
	}
}
