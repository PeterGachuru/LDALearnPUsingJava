package org.example;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPModifyEntry {
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

            // Specify the DN of the entry you want to modify
            String entryDN = "cn=Peter,ou=users," + baseDN; // Change this to the correct DN of the entry

            // Prepare the modifications
            ModificationItem[] mods = new ModificationItem[2];

            // Example: Modify the 'sn' attribute (surname) and 'mail' attribute (email)
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", "UpdatedSurname"));
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", "peter.updated@example.com"));

            // Perform the modification
            ctx.modifyAttributes(entryDN, mods);
            System.out.println("Entry modified successfully.");

            // Close the context
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}