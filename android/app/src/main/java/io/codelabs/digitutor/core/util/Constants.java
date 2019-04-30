package io.codelabs.digitutor.core.util;

/**
 * Contains all constant values required for database references and more
 */
public final class Constants {
    // User database references
    public static final String TUTORS = "tutors";
    public static final String PARENTS = "parents";
    public static final String WARDS = PARENTS + "/%s/wards";   // String.format(WARDS,parent.key);

    // Misc database references
    public static final String SUBJECTS = "subjects";
    public static final String COMPLAINTS = "complaints";
    public static final String TIMETABLES = WARDS + "/%s/timetables"; // String.format(WARDS,parent.key, ward.key);
    public static final String REPORTS = "reports";
    public static final String ASSIGNMENTS = TUTORS + "/%s/assignments";    // String.format(WARDS,tutor.key);

    // Shared Preferences
    public static final String SHARED_PREFS = "TUTOR_SHARED_PREFS";
    public static final String USER_KEY = "USER_KEY";
    public static final String USER_TYPE = "USER_TYPE";
}
