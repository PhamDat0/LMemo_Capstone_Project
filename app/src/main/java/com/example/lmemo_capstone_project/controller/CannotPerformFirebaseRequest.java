package com.example.lmemo_capstone_project.controller;

public class CannotPerformFirebaseRequest extends Throwable {
    public CannotPerformFirebaseRequest(String message) {
        super(message);
    }
}
