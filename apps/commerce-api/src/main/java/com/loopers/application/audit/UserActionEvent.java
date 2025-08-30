package com.loopers.application.audit;

public record UserActionEvent(
    String type, 
    String principal, 
    String target, 
    String detail
) {
    public static UserActionEvent of(String type, String principal, String target, String detail) {
        return new UserActionEvent(type, principal, target, detail);
    }
}
