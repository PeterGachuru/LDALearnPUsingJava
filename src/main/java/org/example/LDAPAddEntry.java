package org.example;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPAddEntry {
    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:389";
        String baseDN = "dc=example,dc=com";
        String userDN = "cn=admin,dc=example,dc=com";
        String password = "admin";

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl + "/" + baseDN);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);

            // Specify the DN for the new entry
            String newUserDN = "cn=John Doe2,ou=users,dc=example,dc=com";

            // Specify the attributes for the new entry
            Attributes attributes = new BasicAttributes();
            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("inetOrgPerson");  // Define the objectClass of the entry
            attributes.put(objectClass);
            attributes.put("cn", "John Doe2");
            attributes.put("sn", "Doe2");
            attributes.put("mail", "johndoe2@example.com");
            attributes.put("uid", "jdoe2");

            // Add the entry to LDAP
            ctx.createSubcontext(newUserDN, attributes);
            System.out.println("Entry added successfully.");

            // Close the context
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
