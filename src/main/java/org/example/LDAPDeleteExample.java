package org.example;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPDeleteExample {
    private String ldapUrl = "ldap://localhost:389";  // Change this if necessary
    private String userDN = "cn=admin,dc=example,dc=com";  // Admin DN
    private String password = "admin";  // Admin password

    // Method to delete an entry in LDAP
    public void deleteEntry(String entryDN) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext ctx = null;

        try {
            ctx = new InitialDirContext(env);
            // Check for child entries
            if (hasChildren(ctx, entryDN)) {
                // If there are child entries, delete them first
                deleteChildren(ctx, entryDN);
            }

            // Now delete the specified entry
            ctx.destroySubcontext(entryDN);
            System.out.println("Successfully deleted entry: " + entryDN);
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Failed to delete entry: " + entryDN + ", Reason: " + e.getMessage());
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to check if an entry has children
    private boolean hasChildren(DirContext ctx, String entryDN) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> results = ctx.search(entryDN, "(objectClass=*)", searchControls);
        return results.hasMore();
    }

    // Method to delete all child entries of a specified DN
    private void deleteChildren(DirContext ctx, String entryDN) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> results = ctx.search(entryDN, "(objectClass=*)", searchControls);

        while (results.hasMore()) {
            SearchResult searchResult = results.next();
            String childDN = searchResult.getNameInNamespace();
            deleteChildren(ctx, childDN);  // Recursively delete children
            ctx.destroySubcontext(childDN);  // Delete the child entry
            System.out.println("Deleted child entry: " + childDN);
        }
    }

    public static void main(String[] args) {
        LDAPDeleteExample ldapDelete = new LDAPDeleteExample();

        // Example: Deleting the base DN
        ldapDelete.deleteEntry("dc=example,dc=com");
    }
}
