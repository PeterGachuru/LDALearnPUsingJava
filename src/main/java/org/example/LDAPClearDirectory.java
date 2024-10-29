package org.example;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPClearDirectory {

    private static DirContext connectLDAP(String ldapUrl, String adminDN, String adminPassword) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);

        return new InitialDirContext(env);
    }

    public static void deleteAllEntries(String ldapUrl, String baseDN, String adminDN, String adminPassword) {
        try {
            DirContext ctx = connectLDAP(ldapUrl, adminDN, adminPassword);

            // Get all entries under the base DN
            deleteSubtree(ctx, baseDN);

            // Optionally, delete the base DN itself if needed
            try {
                ctx.destroySubcontext(baseDN);
                System.out.println("Deleted base DN: " + baseDN);
            } catch (NamingException e) {
                System.out.println("Failed to delete base DN: " + baseDN + ", Reason: " + e.getMessage());
            }

            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static void deleteSubtree(DirContext ctx, String baseDN) throws NamingException {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);  // Only search immediate children

        NamingEnumeration<SearchResult> results = ctx.search(baseDN, "(objectClass=*)", controls);

        while (results.hasMore()) {
            SearchResult result = results.next();
            String dn = result.getNameInNamespace();

            // Recursively delete sub-entries
            deleteSubtree(ctx, dn);

            // Delete this entry after its children are deleted
            try {
                ctx.destroySubcontext(dn);
                System.out.println("Deleted: " + dn);
            } catch (NamingException e) {
                System.out.println("Failed to delete: " + dn + ", Reason: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:389";
        String baseDN = "dc=example,dc=com";  // Base DN of the directory to clear
        String adminDN = "cn=admin,dc=example,dc=com";  // LDAP admin user DN
        String adminPassword = "admin";  // LDAP admin user password

        deleteAllEntries(ldapUrl, baseDN, adminDN, adminPassword);
    }
}
