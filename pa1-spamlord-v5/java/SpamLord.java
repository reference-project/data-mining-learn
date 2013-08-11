// CS124 HW1 SpamLord
import java.util.regex.*;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.*;

public class SpamLord {

  /*
   * You do not need to modify anything in the Contact class.
   * This class encapsulates the basic information associated with
   * an e-mail or phone number for this assignment.  It has three
   * data members:
   *    filename // the name of the file in which the contact item was found
   *    type     // the type of contact information found: either "e" or "p"
   *    value    // the actual string representatino of the e-mail or phone number
   *             // see assignment description for details
   * you can ignore the other functions which are just necessary for correct
   * behavior when used an element of a java.uitl.Set
   */
  static class Contact implements Comparable<Contact>{
    private String fileName;
    private String type;
    private String value;
    public Contact() {}
    public Contact(String fileName,String type,String value) {
      this.fileName = fileName;
      this.type = type;
      // automatically change value to lower case upon construction;
      this.value = value.toLowerCase();
    }
    public String getFileName() {return fileName;}
    public String getType() {return type;}
    public String getValue() {return value;}
    @Override
    public boolean equals(Object o) {
      Contact c = (Contact) o;
      return (fileName.equals(c.fileName) && type.equals(c.type) && value.equals(c.value));
    }
    @Override
    public int hashCode() {
      return 31*fileName.hashCode() + 17*type.hashCode() + value.hashCode();
    }
    public int compareTo(Contact c) {
      int fileNameCmp = fileName.compareTo(c.fileName);
      if (fileNameCmp != 0) {
        return fileNameCmp;
      }
      int typeCmp = type.compareTo(c.type);
      if (typeCmp != 0) {
        return typeCmp;
      }
      return value.compareTo(c.value);
    }
    @Override public String toString() {
      return fileName + "\t" + type + "\t" + value;
    }
  }
  
  // Example pattern for extracting e-mail addresses
  private Pattern ePattern_1 = Pattern.compile("(\\w+(\\.\\w+)?)\\s*(@|\\bat\\b)\\s*(\\w+((\\.|\\bdot\\b|\\bdt\\b)\\w+)?)(\\.|\\bdot\\b|\\bdt\\b|,)\\w{3}");
  private Pattern ePattern_3 = Pattern.compile("(\\w+)?\\sat\\s(\\w+)*\\sdt\\s([\\w]{3})");
  
  private Pattern ePattern_2 = Pattern.compile("obfuscate\\('(.*?)','(.*?)'\\);");
  private Pattern ePattern_4 = Pattern.compile("([\\w\\.]+(?=\\s*\\(.*?\\))).*?\"?@([\\w\\.]*).edu(\"?|(?=&rdquo;))");
  private Pattern ePattern_5 = Pattern.compile("(\\w+)?\\sat\\s(cs stanford edu)");
  
  private Pattern pPattern = Pattern.compile("[(]?(\\d{3})[)]?[-\\s]?(\\d{3})[-\\s](\\d{4}(?!\\d))");
 
  /* 
   * TODO
   * This should return a list of Contact objects found in the input. 
   * You can change anything internal to this function but make sure you
   * leave the interface (arguments and return value) unchanged because
   * it will be directly called by the submission script.
   */  
  public List<Contact> processFile(String fileName, BufferedReader input) {
    List<Contact> contacts = new ArrayList<Contact>();

    // for each line
    Matcher em,pm,em2,em4,em3,em5;
    String email,phone;
    try {
      for(String line = input.readLine(); line != null; line = input.readLine()) {
//    	line = line.replaceAll("\\s*dot\\s*", ".");
//    	line = line.replaceAll("\\s*at\\s*","@");

    	line = line.replaceAll("Apache/[\\.\\d]+\\s\\(Fedora\\)\\sServer","#####");
    	line = line.replaceAll("((?<!&lt)(?<!&#x40)|&ldquo)((;\\s*(?=edu))|(;(?=\\w)))",".");//̎����";"�����.������r �������漰����
    	line = line.replaceAll("%20", " ");                  //�x��ĕr��Ͱ����еġ�%20����Q�ɿո�
    	line = line.replaceAll("(?<!\\d+)-(?!\\d+)", "");    //��Q�á�-����ÿһ���ַ�朽������email��ͬ�r������Q��phone�еġ�-��
    	line = line.replace("WHERE", "at");
    	line = line.replace("DOM", "dot");
    	line = line.replace(" dot ", ".");
    	line = line.replace("&#x40;","@");
    	
    	
        em = ePattern_1.matcher(line);
        em2 = ePattern_2.matcher(line);
        
        em4 = ePattern_4.matcher(line);
        em3 = ePattern_3.matcher(line);
        em5 = ePattern_5.matcher(line);
        
        pm = pPattern.matcher(line);
        
        while(em.find()) {
          email = em.group(1) + "@" + em.group(4) + ".edu";
          Contact contact = new Contact(fileName,"e",email);
          contacts.add(contact);
        }
        while(em2.find()) {
            email = em2.group(2) + "@" + em2.group(1);
            Contact contact = new Contact(fileName,"e",email);
            contacts.add(contact);
          }
        while(em4.find()) {
            email = em4.group(1) + "@" + em4.group(2) + ".edu";
            Contact contact = new Contact(fileName,"e",email);
            contacts.add(contact);
          }
        while(em3.find()) {
            email = em3.group(1) + "@" + em3.group(2) + ".com";
            Contact contact = new Contact(fileName,"e",email);
            contacts.add(contact);
          }
        while(em5.find()) {
        	
            email = em5.group(1) + "@" + em5.group(2).replaceAll(" ", ".");
            Contact contact = new Contact(fileName,"e",email);
            contacts.add(contact);
          }
        

        while(pm.find()){
        	phone = pm.group(1)+"-"+pm.group(2)+"-"+pm.group(3);
        	Contact contact = new Contact(fileName,"p",phone);
        	contacts.add(contact);
        }
      }
      input.close();
    } catch(IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return contacts;
  }

  /*
   * You should not need to edit this, nor should you alter it's interface
   * because it will also be called direclty by the submission  program
   */
  public List<Contact> processDir(String dirName) {
    List<Contact> contacts = new ArrayList<Contact>();
    for(File f: new File(dirName).listFiles()) {
      if (f.getName().startsWith(".")) 
        continue;
      try {
        BufferedReader input = new BufferedReader(new FileReader(f));
        contacts.addAll(processFile(f.getName(), input));
      } catch(IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    return contacts;
  }

  /*
   * You should not need to edit this function
   * It simply reads in a tsv gold file and returns a list of
   * Contacts
   */
  private List<Contact> loadGold(String goldPath) {
    List<Contact> gold = new ArrayList<Contact>();
    try {
      BufferedReader input = new BufferedReader(new FileReader(goldPath));
      
      String[] toks;
      for(String line = input.readLine(); line != null; line = input.readLine()) {
        toks = line.split("\t");
        Contact contact = new Contact(toks[0],toks[1],toks[2]);
        gold.add(contact);
      }
      input.close();

    } catch(IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return gold;
  }

  /*
   * You should not need to edit this.
   * This is just a utility function which turns a Set into
   * a sorted list for convenience when looking at the output.
   */
  private List<Contact> asSortedList(Set<Contact> set) {
    Contact[] c = new Contact[0];
    List<Contact> list = Arrays.asList(set.toArray(c));
    Collections.sort(list);
    return list;
  }

  /*
   * You should not need to edit this.
   * This takes in two Lists of Contacts and prints out the intersection
   * and differences, which can be thought of as true positives, false
   * positives and false negatives.
   */
  private void score(List<Contact> guesses, List<Contact> gold) {
    Set<Contact> guess_set = new HashSet<Contact>();
    guess_set.addAll(guesses);
    Set<Contact> gold_set = new HashSet<Contact>();
    gold_set.addAll(gold);

    Set<Contact> tp = new HashSet<Contact>(guess_set);
    System.out.println("guess_set.size()="+guess_set.size()+"\tgold_set.size()="+gold_set.size());
    tp.retainAll(gold_set);
    List<Contact> tp_list = asSortedList(tp);
    System.out.println("True Positives (" + tp_list.size() +")\t###############################");
    for (Contact contact : tp_list) {
      System.out.println(contact);
    }

    Set<Contact> fp = new HashSet<Contact>(guess_set);
    fp.removeAll(gold_set);
    List<Contact> fp_list = asSortedList(fp);
    System.out.println("False Positives (" + fp_list.size() +")\t###############################");
    for (Contact contact : fp_list) {
      System.out.println(contact);
    }

    Set<Contact> fn = new HashSet<Contact>(gold_set);
    fn.removeAll(guess_set);
    List<Contact> fn_list = asSortedList(fn);
    System.out.println("False Negatives (" + fn_list.size() +")\t###############################");
    for (Contact contact : fn_list) {
      System.out.println(contact);
    }

    System.out.println("Summary: tp=" + tp.size() + "\tfp=" + fp.size() + "\tfn=" + fn.size());
  }

  /* 
   * main takes a directory and a file with the Gold contacts.
   * it processes each file in the directory, extracting any contacts
   * and compares them to the contacts listed in the gold file
   */
  public static void main(String[] args) {
//    if (args.length != 2) {
//      System.err.println("usage:\tSpamLord <data_dir> <gold_file>");
//      System.exit(0);
//    }
    SpamLord vader = new SpamLord();

    
    List<Contact> guesses = vader.processDir("data/dev");
    List<Contact> gold = vader.loadGold("data/devGOLD");
    vader.score(guesses,gold);
  }
}
