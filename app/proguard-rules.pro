# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

    # For XStream
    -dontwarn java.awt.**
    -dontwarn java.beans.**
    -dontwarn javax.xml.stream.**

    # For Groovy (if the issue persists related to Groovy)
    -dontwarn org.apache.ivy.**
