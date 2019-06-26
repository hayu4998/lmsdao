package com.gcit.lms.service;

import com.gcit.lms.ui.TextUI;

public class ServiceStart {

	public static void main(String[] args) {
		TextUI ui = TextUI.getInstance();
		Integer mainChoice = null;
		do{
			
			mainChoice = ui.uiShowMenu();
			if(mainChoice == 4) {
				break;
			}else if(mainChoice == 1) { // Librarian
				LibrarianService librarian = LibrarianService.getInstance();
				librarian.startService();
			}else if(mainChoice == 2) { // Admin
				AdminService admin = AdminService.getInstance();
				admin.startService();
			}else {// borrower
				BorrowService borrow = BorrowService.getInstance();
				borrow.startService();
			}
			
		}while(true);
		
	}

}
