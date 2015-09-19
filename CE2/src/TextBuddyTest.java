import static org.junit.Assert.*;

import org.junit.Test;

public class TextBuddyTest {

	@Test
	public void testAdd() {
		
		// successful add
		TextBuddy tb = new TextBuddy("test.txt");
		assertEquals(TextBuddy.executeCommand("add cats are good!", tb), 
				"added to test.txt: \"cats are good!\"\n");
		TextBuddy.executeCommand("clear", tb);
		
		// nothing is added
		assertEquals(TextBuddy.executeCommand("add ", tb),
				"added to test.txt: absolutely nothing!\n");
	
		System.out.println("Passed all add cases!");
	}
	
	@Test
	public void testDelete() {
		
		// successful delete
		TextBuddy tb = new TextBuddy("test.txt");
		TextBuddy.executeCommand("add cats are really good!", tb);
		assertEquals(TextBuddy.executeCommand("delete 1", tb),
				"deleted from test.txt: \"cats are really good!\"\n");
		
		// unable to delete from empty list
		assertEquals(TextBuddy.executeCommand("delete 1", tb), 
				"test.txt is empty\n");
		
		// deleting from an invalid index in a single item list
		TextBuddy.executeCommand("add cats are really good!", tb);
		assertEquals(TextBuddy.executeCommand("delete 2", tb),
				"You can only delete the first item\n");
		
		// deleting from an invalid index in multiple items list
		TextBuddy.executeCommand("add you're right!", tb);
		
		assertEquals(TextBuddy.executeCommand("delete 3", tb),
				"You can only delete items 1 to 2\n");
		TextBuddy.executeCommand("clear", tb);
		
		System.out.println("Passed all delete cases!");
	}
	
	@Test
	public void testClear() {
		
		// successful clear
		TextBuddy tb = new TextBuddy("test.txt");
		TextBuddy.executeCommand("add to be cleared", tb);
		assertEquals(TextBuddy.executeCommand("clear", tb),
				"all content deleted from test.txt\n");
		
		// clearing with too many arguments
		TextBuddy.executeCommand("add to be cleared", tb);
		assertEquals(TextBuddy.executeCommand("clear everything", tb),
				"There is no need for \"everything\" after the command \"clear\"\n"
				+ "It will be performed anyway\n" + "all content deleted from test.txt\n");
		
		// clearing an empty list
		assertEquals(TextBuddy.executeCommand("clear", tb), 
				"test.txt is empty\n");
	
		System.out.println("Passed all clear cases!");
	}
	
	@Test
	public void testDisplay() {
		
		// successful display
		TextBuddy tb = new TextBuddy("test.txt");
		TextBuddy.executeCommand("add cats go meow", tb);
		TextBuddy.executeCommand("add cats rule the world", tb);
		assertEquals(TextBuddy.executeCommand("display", tb),
				"1. cats go meow\n" + "2. cats rule the world\n");
		
		// displaying with too many arguments
		assertEquals(TextBuddy.executeCommand("display everything", tb),
				"There is no need for \"everything\" after the command \"display\"\n"
				+ "It will be performed anyway\n"
				+ "1. cats go meow\n" + "2. cats rule the world\n");
		TextBuddy.executeCommand("clear", tb);
		
		// displaying an empty list
		assertEquals(TextBuddy.executeCommand("display", tb),
				"test.txt is empty\n");
		
		System.out.println("Passed all display cases!");
	}
	
	
	@Test
	public void testSearch() {
		
		// successful search
		TextBuddy tb = new TextBuddy("test.txt");
		TextBuddy.executeCommand("add cats are awesome", tb);
		TextBuddy.executeCommand("add Really!", tb);
		TextBuddy.executeCommand("add Would you like a cat?", tb);
		assertEquals(TextBuddy.executeCommand("search cat", tb), 
				"1. cats are awesome\n" + "3. Would you like a cat?\n");
		
		// unsuccessful search
		assertEquals(TextBuddy.executeCommand("search dog", tb),
				"\"dog\" not found in test.txt");
		
		TextBuddy.executeCommand("clear", tb);
		System.out.println("Passed all search cases!");
	}
	
}
