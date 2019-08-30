package org.testar.actions;

/**
 * MatchType
 * 
 * <li>EQUALS - compares this string to the specified object
 * <li>EQUALS_IC - compares this string to the specified object, ignoring case
 * <li>STARTSWITH - tests if this string starts with the specified prefix
 * <li>STARTSWITCH_IC - tests if this string starts with the specified prefix, ignoring case
 * <li>CONTAINS - returns true if and only if this string contains the specified sequence of char values
 * <li>CONTAINS_IC - returns true if and only if this string contains the specified sequence of char values, ignore case
 * 
 */
public enum MatchType {
	EQUALS, EQUALS_IC, STARTSWITH, STARTSWITCH_IC, CONTAINS, CONTAINS_IC
}
