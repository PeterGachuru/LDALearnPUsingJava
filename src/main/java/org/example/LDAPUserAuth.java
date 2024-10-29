package org.example;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class LDAPUserAuth {
    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:389"; // LDAP server URL
        String baseDN = "dc=example,dc=com";    // The base DN of the directory
        String userDN = "cn=Peter,ou=users," + baseDN; // User DN you want to authenticate
        String userPassword = "password";       // The user's password

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl); // LDAP URL
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // Simple authentication
        env.put(Context.SECURITY_PRINCIPAL, userDN);        // The user's DN (distinguished name)
        env.put(Context.SECURITY_CREDENTIALS, userPassword); // The user's password

        try {
            // Create the initial directory context
            DirContext ctx = new InitialDirContext(env);

            // If we successfully authenticate and bind, this message will be printed
            System.out.println("Authentication successful!");

            // Close the context
            ctx.close();
        } catch (NamingException e) {
            // Handle authentication failure
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }
}
