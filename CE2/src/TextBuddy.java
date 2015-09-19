import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * This class is used to edit (add, delete, clear) or retreive (display)
 * the contents of a text file. A text file can be created, if it does not
 * already exist. Contents can be added to or deleted from the text file 
 * one line at a time. The text file will be refreshed and saved after every
 * successful command that modifies the text file (add, delete or clear).
 * The class will not auto save the textfile on exit.
 	
 	Assumptions made:
 	1. The class assumes that a valid filename is entered as a command line
 	argument when the class is run on the terminal or command prompt.
 	2. The class will not create a new file if user exits without entering
	a command that modifies (add, delete, clear) a newly createdfile.
	3. Blank spaces or enters entered after the add command will not be added
	to the text file. Such input are treated as trivial and will not be processed.
	4. No autoformatting of the input to be add is done, spaces left by the user 
	between words after the add command will be assumed to be deliberate.

 * @author Cheng Shan A0126473E CS2103T Tutorial T10
 */


public class TextBuddy{

	// Attributes of TextBuddy object
	private String fileName;
	private ArrayList<String> contentStore;

	// These are potential exception messages
	private static final String EXC_FILE_NOT_FOUND = "%1$s not found. Creating a new file %1$s";
	private static final String EXC_PROBLEM_WRITING_FILE = "Problem writing to the file";

	// These are potential error messages
	private static final String ERROR_UNRECOGNIZED_COMMAND = "Unrecognized command type";
	private static final String ERROR_NULL_COMMAND = "Command type string cannot be null!";

	// These are feedback messages that user will encounter when using textBuddy 
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use\n";
	private static final String MESSAGE_INVALID_FILENAME = "No filename entered. Please enter a filename: ";
	private static final String MESSAGE_ENTER_COMMAND = "Enter command: ";
	private static final String MESSAGE_EMPTY_COMMAND = "No command has been entered\n";
	private static final String MESSAGE_INVALID_COMMAND = "\"%1$s\" is not a valid command\n";
	private static final String MESSAGE_COMMAND_HINT = "Valid commands are: add, delete, clear, display, search and exit\n";
	
	private static final String MESSAGE_ADDED = "added to %1$s: %2$s\n";
	private static final String MESSAGE_DELETED = "deleted from %1$s: \"%2$s\"\n";
	private static final String MESSAGE_INVALID_INT = "Invalid index entered. Please enter an integer after \"delete\"\n";
	private static final String MESSAGE_INVALID_INDEX = "You can only delete the first item\n";
	private static final String MESSAGE_INVALID_INDEX_RANGE = "You can only delete items 1 to %1$d\n";	
	private static final String MESSAGE_ALL_CLEARED = "all content deleted from %1$s\n";
	private static final String MESSAGE_EMPTY_FILE = "%1$s is empty\n";
	private static final String MESSAGE_EXTRA_INFO = "There is no need for \"%1$s\" after the command \"%2$s\"\nIt will be performed anyway\n";
	private static final String MESSAGE_KEYWORD_NOT_FOUND = "\"%1$s\" not found in %2$s\n";
	private static final String MESSAGE_NO_KEYWORD_ENTERED = "Please specify keyword(s) after \"search\"\n";


	// These are the possible command types:
	enum CommandType {
		APPEND, DELETE, CLEAR, DISPLAY, SEARCH, INVALID, EXIT
	};

	/* This variable is declared for the whole class
	 * to facilitate automated testing using I/O redirection
	 * if not, only the first line of input will be processed.
	 */
	private static Scanner scanner = new Scanner(System.in);

	/** constructor */
	public TextBuddy(String fileName) {
		this.fileName = fileName;
		contentStore = new ArrayList<String>();
		copyContentsFromFile();
	}

	// Function to append a line of content to end of the text file.
	private String appendContent (String content) {
		if(content.equals("")) {
			return String.format(MESSAGE_ADDED, fileName, "absolutely nothing!");
		}
		
		contentStore.add(content);
		saveContent();
		String formattedContent = "\"" + content + "\"";
		return String.format(MESSAGE_ADDED, fileName, formattedContent);
	}

	// Function to delete a line of content specified by the index of the content 
	private String deleteIndex (int indexOfContent) {
		if(contentStore.isEmpty()) {
			return String.format(MESSAGE_EMPTY_FILE, fileName);
		}

		try{
			// ArrayList index starts at 0
			String content = contentStore.remove(indexOfContent - 1);
			saveContent();
			return String.format(MESSAGE_DELETED, fileName, content);
		} catch (IndexOutOfBoundsException e) {
			if (contentStore.size() == 1){
				return MESSAGE_INVALID_INDEX;
			}
			return String.format(MESSAGE_INVALID_INDEX_RANGE, contentStore.size());
		}
	}

	// Function to clear a non empty list
	private String clearAllContent(String content) {
		if (contentStore.isEmpty()) {
			return String.format(MESSAGE_EMPTY_FILE, fileName);
		}
		
		String extraInfo = checkRedundantArguments(content, "clear");
		contentStore.clear();
		saveContent();
		return extraInfo + String.format(MESSAGE_ALL_CLEARED, fileName);
	}

	// Function to display a non empty list
	private String displayAllContent(String content) {
		String extraInfo = checkRedundantArguments(content, "display");
		if (contentStore.isEmpty()) {
			return extraInfo + String.format(MESSAGE_EMPTY_FILE, fileName);
		}
			
		String contents = "";
		for (int i = 0; i < contentStore.size(); i++) {
        	String nextLine = i + 1 + ". " + contentStore.get(i) + "\n";
        	contents += nextLine;
       	 }
       	return extraInfo + contents;
    }
	
	// Function to search for lines containing a keyword in file
	private String searchForKeyword(String content){
		if (content.isEmpty()) {
			return MESSAGE_NO_KEYWORD_ENTERED;
		}
		
		if (contentStore.isEmpty()) {
			return String.format(MESSAGE_EMPTY_FILE, fileName);
		}
		
		String contents = "";
		for (int i = 0; i < contentStore.size(); i++) {
        	String nextLine = i + 1 + ". " + contentStore.get(i) + "\n";
        	if (contentStore.get(i).contains(content)) {
        		contents += nextLine;
        	}
       	 }
		
		if (contents.isEmpty()) {
			return String.format(MESSAGE_KEYWORD_NOT_FOUND, content, fileName);
		}
       	return contents;
	}
	
	// Function to write ArrayList of contents to text file
	private void saveContent() {		
		try{
			FileOutputStream input = new FileOutputStream(fileName);
        	OutputStreamWriter outputStreamWriter = new OutputStreamWriter(input);    
        	Writer writer = new BufferedWriter(outputStreamWriter);
        	for(int i = 0; i < contentStore.size(); i++) {
        		writer.write(contentStore.get(i) + "\n");
        	}
        	writer.close();
        } catch (IOException e) {
            showExceptionMessage(String.format(EXC_PROBLEM_WRITING_FILE, fileName));
        }					
	}
		
	// Function to write contents of (existent) text file to ArrayList
	private void copyContentsFromFile() {
		try{
			File textFile = new File(fileName);
			Scanner sc = new Scanner(textFile);
			while(sc.hasNextLine()){
				contentStore.add(sc.nextLine());
			}
			sc.close();
		} catch (IOException e) {
            showExceptionMessage(String.format(EXC_FILE_NOT_FOUND, fileName));
        }
	}

	// Function to extract the command content for append and delete commands
	private static String removeFirstWord(String userCommand) {
		String commandContent = userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
		return commandContent;
	}

	// Function to extract command from line of user input
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

	// Function to check for extra arguments after clear and display commands
	private static String checkRedundantArguments(String commandContent, String command) {
		if (!commandContent.trim().equals("")) {
			return String.format(MESSAGE_EXTRA_INFO, commandContent, command);
		}

		return "";
	}

	// Function to display errors
	private static void showExceptionMessage(String errorMessage) {
		System.err.println(errorMessage);
	}


	public static void main(String[] args) {
		
		String textFileName = checkEmptyFileName(args);
		TextBuddy list = new TextBuddy(textFileName);
		showToUser(String.format(MESSAGE_WELCOME, textFileName));
		
		while(true) {
			showToUser(MESSAGE_ENTER_COMMAND);
			String userCommand = scanner.nextLine();
			String feedback = executeCommand(userCommand, list);
			showToUser(feedback);
		}
	}

	// Function to check for valid args[0] input
	public static String checkEmptyFileName(String[] arguments){
		String validFileName = "";
		if (arguments.length > 0) {
			return arguments[0];
		}
		while (true) {
			showToUser(MESSAGE_INVALID_FILENAME);
			validFileName += scanner.nextLine();
			if (!validFileName.isEmpty()) {
				break;
			}
		}
		return validFileName;
	}
	
	// Function to execute a user command on a TextBuddy object
	public static String executeCommand(String userCommand, TextBuddy textList) {
		if (userCommand.trim().equals("")) {
			return MESSAGE_EMPTY_COMMAND + MESSAGE_COMMAND_HINT;
		}

		String commandTypeString = getFirstWord(userCommand);
		String commandContent = removeFirstWord(userCommand);

		CommandType commandType = determineCommandType(commandTypeString);

		switch(commandType) {
			case APPEND:
				return textList.appendContent(commandContent);
			case DELETE:
				try {
					return textList.deleteIndex(Integer.parseInt(commandContent));
				} catch (NumberFormatException e) {
					return MESSAGE_INVALID_INT;
				}
			case CLEAR:
				return textList.clearAllContent(commandContent);
			case DISPLAY:
				return textList.displayAllContent(commandContent);
			case SEARCH:
				return textList.searchForKeyword(commandContent);
			case INVALID:
				return String.format(MESSAGE_INVALID_COMMAND, userCommand) + MESSAGE_COMMAND_HINT;
			case EXIT:
				System.exit(0);
			default:
				// throw new Error if the command is not recognized
				throw new Error(ERROR_UNRECOGNIZED_COMMAND);
		}
	}

	// @param commandTypeString is the first word of user's command
	private static CommandType determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error(ERROR_NULL_COMMAND);
		}

		if (commandTypeString.equalsIgnoreCase("add")) {
			return CommandType.APPEND;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("search")) { 
			return CommandType.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return CommandType.EXIT;
		} else {
			return CommandType.INVALID;
		}
	}

	// Function to display text to users in main
	public static void showToUser(String message) {
		System.out.print(message);
	}

}