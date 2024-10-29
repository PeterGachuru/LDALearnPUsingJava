package org.example.auth;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;

public class LDAPUserManager {

    private DirContext ldapContext;

    // Initialize LDAP connection
    public LDAPUserManager(String ldapUrl, String adminDN, String adminPassword) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);  // LDAP server URL
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);  // Admin DN
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);  // Admin password

        // Create LDAP context


        try {
            ldapContext = new InitialDirContext(env);
            System.out.println("Authentication successful!");
        } catch (NamingException e) {
            e.printStackTrace(); // Print error details
        }
    }

    // Function to hash the password using SSHA
    private String hashPasswordSSHA(String password) throws NoSuchAlgorithmException {
        // Generate a random 4-byte salt
        byte[] salt = new byte[4];
        new Random().nextBytes(salt);

        // Create SSHA digest
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(password.getBytes(StandardCharsets.UTF_8));
        sha.update(salt);

        byte[] digest = sha.digest();
        byte[] digestAndSalt = new byte[digest.length + salt.length];
        System.arraycopy(digest, 0, digestAndSalt, 0, digest.length);
        System.arraycopy(salt, 0, digestAndSalt, digest.length, salt.length);

        // Encode in Base64
        return "{SSHA}" + Base64.getEncoder().encodeToString(digestAndSalt);
    }

    // Function to add a new user to LDAP
    public void addUser(String department, String uid, String firstName, String lastName, String password) throws NamingException, NoSuchAlgorithmException {
        // Construct the DN for the new user
        String userDN = "uid=" + uid + ",ou=" + department + ",dc=example,dc=com";

        // Set the attributes for the new user
        Attributes attributes = new BasicAttributes();
        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("inetOrgPerson");  // Standard object class for user accounts
        objectClass.add("organizationalPerson");
        objectClass.add("person");
        objectClass.add("top");

        attributes.put(objectClass);
        attributes.put("uid", uid);  // Unique identifier
        attributes.put("cn", firstName + " " + lastName);  // Full name
        attributes.put("sn", lastName);  // Surname
        String hashedPassword = hashPasswordSSHA(password);
        attributes.put("userPassword", hashedPassword);  // Encrypted password using SSHA

        // Add the entry to LDAP
        ldapContext.createSubcontext(userDN, attributes);
        System.out.println("User added successfully: " + userDN);
        System.out.println("Password: " + password+", hashed: "+hashedPassword);
    }

    public boolean authenticateUser(String uid, String password) {
        String userDN = "uid=" + uid + ",ou=users,dc=example,dc=com";  // Adjust your DN accordingly
        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");  // Update with your LDAP URL
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDN);  // The user's DN
        env.put(Context.SECURITY_CREDENTIALS, password);  // The user's password

        try {
            // Attempt to authenticate (bind)
            DirContext userContext = new InitialDirContext(env);
            userContext.close();  // Close context after authentication
            System.out.println("Authentication successful for user: " + uid);
            return true;
        } catch (NamingException e) {
            // Authentication failed
            System.out.println("Authentication failed for user: " + uid);
            e.printStackTrace();
            return false;
        }
    }

    // Function to generate a login token (UUID)
    public String generateToken() {
        // Generate a random UUID (this can be a session token or auth code)
        return UUID.randomUUID().toString();
    }

    // Function to login a user and return a token if successful
    public String loginUser(String uid, String password) {
        boolean isAuthenticated = authenticateUser(uid, password);

        if (isAuthenticated) {
            // If authentication succeeds, generate and return a token
            String token = generateToken();
            System.out.println("Generated token: " + token);
            return token;
        } else {
            System.out.println("Login failed for user: " + uid);
            return null;
        }
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
            LDAPUserManager ldapManager = new LDAPUserManager("ldap://localhost:389",
                    "cn=admin,dc=example,dc=com", "admin");

            // Add a user to the IT department
            ldapManager.addUser("IT", "john.doe1", "John", "Doe1", "password123");

            // Add a user to the HR department
            ldapManager.addUser("HR", "jane.smith", "Jane", "Smith", "password456");


            // Attempt to login the user
            String token = ldapManager.loginUser("john.doe1", "password123");

            if (token != null) {
                System.out.println("Login successful. Token: " + token);
            } else {
                System.out.println("Login failed.");
            }

            // Close the connection
            ldapManager.close();
        } catch (NamingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
