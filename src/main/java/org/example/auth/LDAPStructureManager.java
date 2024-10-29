package org.example.auth;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPStructureManager {

    private DirContext ldapContext;

    // Initialize LDAP connection
    public LDAPStructureManager(String ldapUrl, String adminDN, String adminPassword) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);  // LDAP server URL
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);  // Admin DN
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);  // Admin password

        // Create LDAP context
        ldapContext = new InitialDirContext(env);
    }

    // Method to check if an entry exists
    private boolean entryExists(String dn) {
        try {
            ldapContext.getAttributes(dn);
            return true; // Entry exists
        } catch (NamingException e) {
            return false; // Entry does not exist
        }
    }

    // Method to create a domain component (dc)
    public void createDomainComponent(String dc, String organizationName) throws NamingException {
        String dn = "dc=" + dc + ",dc=com";

        if (entryExists(dn)) {
            System.out.println("Domain component already exists: " + dn);
            return;
        }

        Attributes attributes = new BasicAttributes();
        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("dcObject");
        objectClass.add("organization");

        attributes.put(objectClass);
        attributes.put("dc", dc);  // Domain component
        attributes.put("o", organizationName);  // Organization name

        // Create the domain component
        ldapContext.createSubcontext(dn, attributes);
        System.out.println("Domain component created: " + dn);
    }

    // Method to create an organizational unit (ou)
    public void createOrganizationalUnit(String ouName, String parentDN) throws NamingException {
        String dn = "ou=" + ouName + "," + parentDN;

        if (entryExists(dn)) {
            System.out.println("Organizational unit already exists: " + dn);
            return;
        }

        Attributes attributes = new BasicAttributes();
        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("organizationalUnit");

        attributes.put(objectClass);
        attributes.put("ou", ouName);  // Organizational unit name

        // Create the organizational unit
        ldapContext.createSubcontext(dn, attributes);
        System.out.println("Organizational unit created: " + dn);
    }

    // Close the LDAP context
    public void close() throws NamingException {
        if (ldapContext != null) {
            ldapContext.close();
        }
    }

    public static void main(String[] args) {
        try {
            // Initialize LDAP connection
            LDAPStructureManager structureManager = new LDAPStructureManager("ldap://localhost:389",
                    "cn=admin,dc=example,dc=com", "admin");

            // Create the domain component (dc=example,dc=com)
            structureManager.createDomainComponent("example", "Example Company");

            // Create organizational units under dc=example,dc=com
            structureManager.createOrganizationalUnit("IT", "dc=example,dc=com");
            structureManager.createOrganizationalUnit("HR", "dc=example,dc=com");

            // Close the LDAP connection
            structureManager.close();

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
