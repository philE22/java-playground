package com.example.javaplayground.sealed;

sealed interface Loan permits SecuredLoan, UnsecuredLoan {}

final class SecuredLoan implements Loan {}

record UnsecuredLoan(int interest) implements Loan {}