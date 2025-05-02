# Exercise 1 Description

## Overview
In this exercise, you will practice Test-Driven Development (TDD) while refactoring a simple bank account system (seen in class) to make it more flexible and open to future changes.

## Starting Code
You are given two classes:
- `BankAccount`: A public-facing account that enforces withdrawal rules and adds a transaction fee
- `CoreBankAccount`: An internal class that handles the basic account operations

The current implementation has the following limitations:
1. The withdrawal fee calculation is hardcoded (always 1 unit)
2. The withdrawal policy is inflexible (cannot withdraw more than the available balance)
3. The system cannot easily support different types of accounts with varying policies

## Exercise Goals
Your task is to refactor the code following the "Anticipation of Change" principle to make it easier to:
1. Modify the fee calculation logic independently
2. Change the withdrawal policy separately
3. Support multiple account types with different behaviors

## Requirements

### Part 1: Initial Refactoring
1. Rename the existing `BankAccount` class to `SilverBankAccount`
2. Create a common `BankAccount` interface that defines the public API
3. Modify `SilverBankAccount` to implement this interface
4. Replace the hardcoded dependency on `CoreBankAccount` with dependency injection

### Part 2: New Account Types
Create two additional account types:
1. **Gold Account**:
    - No transaction fee (fee = 0)
    - Allows overdraft up to 500 units (can have negative balance up to -500)

2. **Bronze Account**:
    - Conditional fee: if withdrawal amount < 100, fee = 0; otherwise fee = 1
    - No overdraft allowed (balance must always be >= 0)
