package org.example;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPListEntries {
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

            // Set up search controls
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);  // Search the whole subtree
            String searchFilter = "(objectClass=*)";  // Match all entries

            // Perform the search
            NamingEnumeration<SearchResult> results = ctx.search(baseDN, searchFilter, searchControls);

            // Iterate through search results
            while (results.hasMore()) {
                System.out.println("----------------------------");
                SearchResult searchResult = results.next();
                System.out.println("DN: " + searchResult.getNameInNamespace());
                Attributes attributes = searchResult.getAttributes();

                // Print the attributes
                NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
                while (allAttributes.hasMore()) {
                    Attribute attribute = allAttributes.next();
                    System.out.println(attribute.getID() + ": " + attribute.get());
                }
            }

            // Close the context
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
