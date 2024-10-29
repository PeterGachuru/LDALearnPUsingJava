package org.example.auth;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPBookManager {

    private DirContext ldapContext;

    // Initialize LDAP connection
    public LDAPBookManager(String ldapUrl, String adminDN, String adminPassword) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);  // LDAP server URL
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);  // Admin DN
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);  // Admin password

        try {
            ldapContext = new InitialDirContext(env);
            System.out.println("Authentication successful!");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Create 'ou=Books' if it does not exist
    public void createOrganizationalUnit(String ouName) throws NamingException {
        // Construct the DN for the organizational unit
        String ouDN = "ou=" + ouName + ",dc=example,dc=com";

        // Check if the organizational unit already exists
        try {
            ldapContext.getAttributes(ouDN);
            System.out.println(ouName + " already exists");
        } catch (NamingException e) {
            // Create 'ou=Books' if it doesn't exist
            Attributes attributes = new BasicAttributes();
            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("organizationalUnit");
            attributes.put(objectClass);
            attributes.put("ou", ouName);

            ldapContext.createSubcontext(ouDN, attributes);
            System.out.println("Created organizational unit: " + ouDN);
        }
    }

    // Function to add a new book to LDAP
    public void addBook(String title, String author, String isbn, String publishedDate) throws NamingException {
        // First, ensure 'ou=Books' exists
        createOrganizationalUnit("Books");

        // Construct the DN for the new book
        String bookDN = "cn=" + title + ",ou=Books,dc=example,dc=com";

        // Set the attributes for the new book
        Attributes attributes = new BasicAttributes();
        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("inetOrgPerson");  // Standard object class that supports cn

        attributes.put(objectClass);
        attributes.put("cn", title);  // Title of the book (common name)
        attributes.put("sn", author);  // Surname mapped to author
        attributes.put("description", "ISBN: " + isbn + ", Published Date: " + publishedDate);  // Additional book details

        // Add the book entry to LDAP
        ldapContext.createSubcontext(bookDN, attributes);
        System.out.println("Book added successfully: " + bookDN);
    }

    // Close the LDAP context
    public void close() throws NamingException {
        if (ldapContext != null) {
            ldapContext.close();
        }
    }

    public static void main(String[] args) {
        try {
            // Initialize the LDAP connection
            LDAPBookManager ldapManager = new LDAPBookManager("ldap://localhost:389",
                    "cn=admin,dc=example,dc=com", "admin");

            // Add books to the LDAP directory
            ldapManager.addBook("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "1925-04-10");
            ldapManager.addBook("To Kill a Mockingbird", "Harper Lee", "9780060935467", "1960-07-11");

            // Close the connection
            ldapManager.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
