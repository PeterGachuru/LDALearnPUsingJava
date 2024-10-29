package org.example;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPDelete {
    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:389";
        String baseDN = "dc=example,dc=com";
        String userDN = "cn=admin,dc=example,dc=com";
        String password = "admin";

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);

            // Specify the DN of the entry you want to delete
            String entryDN = "cn=Peter,ou=users," + baseDN; // Change this to the correct DN of the entry

            // Perform the deletion
            ctx.destroySubcontext(entryDN);
            System.out.println("Entry deleted successfully.");

            // Close the context
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
