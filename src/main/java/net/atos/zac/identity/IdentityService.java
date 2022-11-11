/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.identity;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

@ApplicationScoped
public class IdentityService {

    private static final String USER_ID_ATTRIBUTE = "cn";

    private static final String USER_FIRST_NAME_ATTRIBUTE = "givenName";

    private static final String USER_LAST_NAME_ATTRIBUTE = "sn";

    private static final String USER_MAIL_ATTRIBUTE = "mail";

    private static final String[] USER_ATTRIBUTES = {USER_ID_ATTRIBUTE, USER_FIRST_NAME_ATTRIBUTE, USER_LAST_NAME_ATTRIBUTE, USER_MAIL_ATTRIBUTE};

    private static final String GROUP_ID_ATTRIBUTE = "cn";

    private static final String GROUP_NAME_ATTRIBUTE = "description";

    private static final String GROUP_MAIL_ATTRIBUTE = "email";

    private static final String[] GROUP_ATTRIBUTES = {GROUP_ID_ATTRIBUTE, GROUP_NAME_ATTRIBUTE, GROUP_MAIL_ATTRIBUTE};

    private static final String GROUP_MEMBER_ATTRIBUTE = "uniqueMember";

    private static final String[] GROUP_MEMBERSHIP_ATTRIBUTES = {GROUP_MEMBER_ATTRIBUTE};

    private static final String USER_OBJECT_CLASS = "inetOrgPerson";

    private static final String GROUP_OBJECT_CLASS = "groupOfUniqueNames";

    private Hashtable<String, String> environment = new Hashtable<String, String>();

    @Inject
    @ConfigProperty(name = "LDAP_DN")
    private String usersDN;

    @Inject
    @ConfigProperty(name = "LDAP_DN")
    private String groupsDN;

    public IdentityService() {
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, getConfig().getValue("LDAP_URL", String.class));
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, getConfig().getValue("LDAP_USER", String.class));
        environment.put(Context.SECURITY_CREDENTIALS, getConfig().getValue("LDAP_PASSWORD", String.class));
    }

    public List<User> listUsers() {
        final String filter = String.format("(&(objectClass=%s))", USER_OBJECT_CLASS);
        return search(usersDN, filter, USER_ATTRIBUTES).stream()
                .map(this::convertToUser)
                .sorted(Comparator.comparing(User::getFullName))
                .toList();
    }

    public List<Group> listGroups() {
        final String filter = String.format("(&(objectClass=%s))", GROUP_OBJECT_CLASS);
        return search(groupsDN, filter, GROUP_ATTRIBUTES).stream()
                .map(this::convertToGroup)
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public User readUser(final String userId) {
        final String filter = String.format("(&(objectClass=%s)(cn=%s))", USER_OBJECT_CLASS, userId);
        return search(usersDN, filter, USER_ATTRIBUTES).stream()
                .findAny()
                .map(this::convertToUser)
                .orElse(new User(userId));
    }

    public Group readGroup(final String groupId) {
        final String filter = String.format("(&(objectClass=%s)(cn=%s))", GROUP_OBJECT_CLASS, groupId);
        return search(groupsDN, filter, GROUP_ATTRIBUTES).stream()
                .findAny()
                .map(this::convertToGroup)
                .orElse(new Group(groupId));

    }

    public List<User> listUsersInGroup(final String groupId) {
        final String filter = String.format("(&(objectClass=%s)(cn=%s))", GROUP_OBJECT_CLASS, groupId);
        return search(groupsDN, filter, GROUP_MEMBERSHIP_ATTRIBUTES).stream()
                .map(this::convertToMembers)
                .flatMap(this::readUsers)
                .sorted(Comparator.comparing(User::getFullName))
                .toList();
    }

    private Stream<User> readUsers(final List<String> userIds) {
        final StringBuilder filter = new StringBuilder();
        filter.append(String.format("(&(objectClass=%s)(|", USER_OBJECT_CLASS));
        userIds.forEach(userId -> filter.append(String.format("(cn=%s)", userId)));
        filter.append("))");
        return search(usersDN, filter.toString(), USER_ATTRIBUTES).stream()
                .map(this::convertToUser);
    }

    private User convertToUser(final Attributes attributes) {
        return new User(readAttributeToString(attributes, USER_ID_ATTRIBUTE),
                        readAttributeToString(attributes, USER_FIRST_NAME_ATTRIBUTE),
                        readAttributeToString(attributes, USER_LAST_NAME_ATTRIBUTE),
                        readAttributeToString(attributes, USER_MAIL_ATTRIBUTE));
    }

    private Group convertToGroup(final Attributes attributes) {
        return new Group(readAttributeToString(attributes, GROUP_ID_ATTRIBUTE),
                         readAttributeToString(attributes, GROUP_NAME_ATTRIBUTE),
                         readAttributeToString(attributes, GROUP_MAIL_ATTRIBUTE));
    }

    private List<String> convertToMembers(final Attributes attributes) {
        return readAttributeToListOfStrings(attributes, GROUP_MEMBER_ATTRIBUTE).stream()
                .map(member -> substringBetween(member, "cn=", ","))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Attributes> search(final String root, final String filter, final String[] attributesToReturn) {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(attributesToReturn);
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        try {
            final DirContext dirContext = new InitialDirContext(environment);
            final NamingEnumeration<SearchResult> namingEnumeration = dirContext.search(root, filter, searchControls);
            final List<Attributes> attributesList = new LinkedList<>();
            while (namingEnumeration.hasMore()) {
                attributesList.add(namingEnumeration.next().getAttributes());
            }
            dirContext.close();
            return attributesList;
        } catch (final NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private String readAttributeToString(final Attributes attributes, final String attributeName) {
        try {
            final Attribute attribute = attributes.get(attributeName);
            return attribute != null ? attribute.get().toString() : null;
        } catch (final NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readAttributeToListOfStrings(final Attributes attributes, final String attributeName) {
        try {
            final List<String> strings = new LinkedList<>();
            final Attribute attribute = attributes.get(attributeName);
            if (attribute != null) {
                final NamingEnumeration<?> enumeration = attribute.getAll();
                while (enumeration.hasMore()) {
                    strings.add(enumeration.next().toString());
                }
            }
            return strings;
        } catch (final NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
