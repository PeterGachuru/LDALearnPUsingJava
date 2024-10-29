package org.example.auth;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class LDAPAuthenticationTest {
    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:389"; // Your LDAP URL
        String adminDN = "cn=admin,dc=example,dc=com"; // Your Admin DN
        String password = "admin"; // Your Admin Password

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            InitialDirContext context = new InitialDirContext(env);
            System.out.println("Authentication successful!");
            context.close();
        } catch (NamingException e) {
            e.printStackTrace(); // Print error details
        }
    }
}
