package org.example.auth;


//import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeAll;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;


import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;


//import static org.junit.jupiter.api.Assertions.*;

public class UserLoginTests {
    private static String ldapUrl;
    private static String adminDN;
    private static String adminPassword;

    @BeforeAll
    public static void setup() {
        ldapUrl = "ldap://localhost:389"; // Your LDAP URL
        adminDN = "cn=admin,dc=example,dc=com"; // Your Admin DN
        adminPassword = "admin"; // Your Admin Password
    }

    private InitialDirContext authenticateUser(String userDN, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        return new InitialDirContext(env);
    }

    @Test
    public void testSuccessfulLogin() {
        String userDN = "uid=john.doe1,ou=IT,dc=example,dc=com"; // Change as necessary
//        String userDN = "uid=john.doe1,dc=example,dc=com"; // Change as necessary
        String password = "password123"; // Change to the correct password for the user

        try {
            InitialDirContext context = authenticateUser(userDN, password);
            assertNotNull(context);
            context.close();
        } catch (NamingException e) {
            fail("Authentication failed: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidCredentials() {
        String userDN = "uid=john.doe,ou=IT,dc=example,dc=com"; // Change as necessary
        String password = "wrongpassword"; // Intentionally incorrect password

        NamingException exception = assertThrows(NamingException.class, () -> {
            authenticateUser(userDN, password);
        });

        assertEquals("LDAP: error code 49 - Invalid Credentials", exception.getMessage());
    }

    @Test
    public void testUserNotFound() {
        String userDN = "uid=nonexistentuser,ou=IT,dc=example,dc=com"; // Change as necessary
        String password = "userpassword"; // Any password

        NamingException exception = assertThrows(NamingException.class, () -> {
            authenticateUser(userDN, password);
        });

        assertEquals("LDAP: error code 32 - No Such Object", exception.getMessage());
    }
}
