package com.example.macros;

public class UserProfileRecord {
    private UserCreds userCreds;
    private UserGoalMacros userGoalMacros;

    public UserCreds getUserCreds() {
        return userCreds;
    }

    public void setUserCreds(UserCreds userCreds) {
        this.userCreds = userCreds;
    }

    public UserGoalMacros getUserGoalMacros() {
        return userGoalMacros;
    }

    public void setUserGoalMacros(UserGoalMacros userGoalMacros) {
        this.userGoalMacros = userGoalMacros;
    }

    public UserProfileRecord() {
    }

    public UserProfileRecord(UserCreds userCreds, UserGoalMacros userGoalMacros) {
        this.userCreds = userCreds;
        this.userGoalMacros = userGoalMacros;
    }
}
