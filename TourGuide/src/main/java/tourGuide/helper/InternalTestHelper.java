package tourGuide.helper;

public class InternalTestHelper {

    // Used for generate the chosen number of users when testing the application

    private static int internalUserNumber = 100000;

    public static void setInternalUserNumber(int internalUserNumber) {
        InternalTestHelper.internalUserNumber = internalUserNumber;
    }
    public static int getInternalUserNumber() {
        return internalUserNumber;
    }

}
