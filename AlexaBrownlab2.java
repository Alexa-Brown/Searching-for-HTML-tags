/* Alexa Brown
Lab 2, using trees, regular expressions, and URL connections
February 2020
*/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.regex.*;
//import org.jsoup.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;

public class AlexaBrownlab2 {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        String URLname;

        System.out.println("Enter full URL name: ");
        URLname = keyboard.next(); //have to create a url object using this name

        StringBuilder inputData = new StringBuilder();
        try {
            //read in the document at the URl
            URL mydataURL = new URL(URLname);
            URLConnection mydataConnection = mydataURL.openConnection();
            BufferedReader mydata = new BufferedReader(new InputStreamReader(mydataConnection.getInputStream()));
            //now don't have to deal with connections, just the buffered reader

            String inputLine;
            while((inputLine = mydata.readLine()) != null){ //gathering the data from the URL page
                inputData.append(inputLine);
            }

            mydata.close();
        }
        catch(Exception e){
            System.out.println("Aborting during reading from URL");
            System.exit(5);
        }

        //using regular expression to find tag
        Pattern patt = Pattern.compile("<(/?)([a-zA-z]+)"); //tag must be < with maybe a / and only letters, no other characters
        Matcher match = patt.matcher(inputData.toString()); //change from StringBuilder back to String
        Stack <String> mystack = new Stack<>(); //stack to hold the tags found in the website

        //set of matched tags, built using tree
        Set<String> tags = new TreeSet<String>(); //Set is an interface so implemented as a TreeSet
        //binary search tree
        try {
            Scanner tagsfile = new Scanner(new FileReader("interestedtags"));
            while (tagsfile.hasNext()) { //bring the tags in from the file
                tags.add(tagsfile.nextLine());
            }
        }

        catch (FileNotFoundException e) { //if the file requested doesn't exist
            System.out.println("File not found.");
        }

        while(match.find()){ //finding tags in the website given
            //System.out.println(match.group(0));
            //System.out.println(match.group(1));
            //System.out.println(match.group(2));
            String token0 = match.group(0); //the entire string
            String token2 = match.group(2).toLowerCase(); //only the letters in the tag

            if (!tags.contains(token2)){ //if the name does not belong to the tag set
                continue;
            }

            if(token0.charAt(0) == '<' && token0.charAt(1) != '/'){ //if it is an open tag
                mystack.push(token2);
                //System.out.println("pushed"); //for debugging

            }
            else if ((!mystack.isEmpty()) && mystack.peek().equals(token2)){ //if it is a closed tag and has a match at the top of the stack
                mystack.pop();
                //System.out.println("popped"); //for debugging
            }

            else{ //closed tag but no match so must be a grammar error
                 System.out.println("Closed tag without a match detected: " + token0);
                 break; //don't push closed tag on to the stack because it delays the detection of the problem
            }
        }

        //after the entire html page has been searched through for tags
        if(mystack.isEmpty()){
            System.out.println("The stack is empty."); //there are no grammar errors
        }

        else {
            System.out.println("There are grammar errors with these tags: ");
            while (!mystack.isEmpty()) { //prints the grammar errors
                System.out.println(mystack.pop());
            }
        }

    } //end of main
}