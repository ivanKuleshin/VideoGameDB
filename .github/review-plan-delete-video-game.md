# Fix Plan: DeleteVideoGameComponentTest Code Review

## Overview
This document outlines the comprehensive plan to address all findings from the code review of `DeleteVideoGameComponentTest.java`. The plan is organized by priority and categorized by fix complexity.

---

## Executive Summary

**Current Status**: 2 test methods with positive scenarios only
**Issues Found**: 5 (High: 1, Medium: 2, Low: 2)
**Missing Coverage**: Negative test scenarios (non-existent games, invalid IDs)
**Estimated Implementation Time**: 2-3 hours

---

## Issue Breakdown & Fix Plan

### HIGH PRIORITY

#### 1. Add Negative Test Coverage (Issue #3)
- **Severity**: HIGH
- **Current State**: Only positive test scenarios exist (happy path deletion and GET all verification)
- **Impact**: Missing critical test coverage for error conditions

**Required Changes**:

1. **Add new test method: `deleteNonExistentVideoGameTest()`**
   - Tests deleting a game with ID that doesn't exist in database
   - Expected: HTTP 404 response
   - Expected response: Error message indicating game not found
   - Location: New test method in `DeleteVideoGameComponentTest`
   - Tickets: Assume XSP-127 (to be confirmed)

2. **Add new test method: `deleteVideoGameWithInvalidIdTest()`**
   - Tests deleting with invalid ID formats (negative, zero, non-numeric)
   - Expected: HTTP 400 Bad Request
   - Location: New test method in `DeleteVideoGameComponentTest`
   - Tickets: Assume XSP-128 (to be confirmed)

3. **Add new test method: `deleteVideoGameWithoutAuthorizationTest()`**
   - Tests deletion without Authorization header (like in GetVideoGameByIdComponentTest)
   - Expected: HTTP 401 Unauthorized
   - Uses: `httpClient.getWithoutAuth()` pattern
   - Location: New test method in `DeleteVideoGameComponentTest`
   - Tickets: Assume XSP-129 (to be confirmed)

**Implementation Pattern** (from GetVideoGameByIdComponentTest):
```
@Test
@TmsLink("XSP-127")
@DisplayName("DeleteVideoGame – Non-existent game returns 404")
void deleteNonExistentVideoGameTest() {
    // Given
    int nonExistentGameId = 99999;
    
    // When
    Response response = AllureSteps.logStepAndReturn(log, ...
    
    // Then
    AllureSteps.logStep(log, "Verify response status code is 404", ...
    AllureSteps.logStep(log, "Verify error message in response", ...
}
```

---

### MEDIUM PRIORITY

#### 2. Refactor AfterEach Cleanup Logic (Issue #1)
- **Severity**: MEDIUM
- **Current Issue**: Unconditionally deletes both PRIMARY_GAME and SECONDARY_GAME regardless of which was used
- **Root Cause**: No tracking of which games were actually inserted/need cleanup

**Options**:

**Option A (Recommended)**: Add BeforeEach for clean state
```java
@BeforeEach
void setup() {
    // Ensure clean state - delete if exists (no-op if not)
    dbClient.deleteVideoGameById(PRIMARY_GAME.getId());
    dbClient.deleteVideoGameById(SECONDARY_GAME.getId());
}

// Remove @AfterEach entirely
```
**Pros**: Explicit about test data dependencies, aligns with AAA pattern
**Cons**: Requires verify test data cleanup doesn't fail

**Option B**: Make cleanup explicit per test
- Remove `@AfterEach` method
- Add cleanup to each test method in the Then section
**Pros**: Very explicit
**Cons**: Code duplication across tests

**Recommendation**: **Option A (BeforeEach)** - aligns with existing test patterns and is more maintainable

#### 3. Improve Response Body Assertions (Issue #4)
- **Severity**: MEDIUM
- **Current State**: Only asserts `status` field in response
- **Missing**: Validation of complete response structure

**Required Changes**:

1. **Verify DeleteVideoGameResponseModel has all expected fields**
   - Add assertion for response model is not null
   - Add assertions for all response fields (not just status)
   - Consider if response should include deleted game ID or other metadata

2. **Update assertions in both existing tests**
   - Line 50-54 in `deleteExistingVideoGameReturns200WithSuccessBodyTest()`
   - Ensure response object is fully validated

**Implementation**:
```java
AllureSteps.logStep(log, "Verify response body is valid and complete",
    () -> {
        DeleteVideoGameResponseModel responseModel = response.as(DeleteVideoGameResponseModel.class);
        assertThat(responseModel)
            .as("Response model should not be null")
            .isNotNull();
        assertThat(responseModel.getStatus())
            .as("Response status message should be 'Record Deleted Successfully'")
            .isEqualTo(EXPECTED_DELETE_STATUS);
        // Add assertions for other fields if they exist
    });
```

---

### LOW PRIORITY

#### 4. Add HTTP Status Code Constant (Issue #6)
- **Severity**: LOW
- **Current Issue**: Magic number `200` used in assertions
- **Location**: Lines 47, 70, and new negative test assertions

**Required Changes**:

1. **Add constant to DeleteVideoGameBaseTest**
```java
protected static final int EXPECTED_SUCCESS_STATUS = 200;
protected static final int EXPECTED_NOT_FOUND_STATUS = 404;
protected static final int EXPECTED_BAD_REQUEST_STATUS = 400;
protected static final int EXPECTED_UNAUTHORIZED_STATUS = 401;
```

2. **Replace all hardcoded status codes in tests**
   - `200` → `EXPECTED_SUCCESS_STATUS`
   - `404` → `EXPECTED_NOT_FOUND_STATUS` (new tests)
   - `400` → `EXPECTED_BAD_REQUEST_STATUS` (new tests)
   - `401` → `EXPECTED_UNAUTHORIZED_STATUS` (new tests)

#### 5. Simplify Assertion Failure Messages (Issue #5)
- **Severity**: LOW
- **Current Issue**: Redundant messages in AllureSteps description and assertion `as()` clause
- **Example**: Line 47-48 has "Verify response status code is 200" in step and "Response status code should be 200" in assertion

**Required Changes**:

1. **Review assertion messages** (lines 47-48, 70-71, similar in new tests)
   - Simplify `as()` messages to avoid duplication with step description
   - Keep them complementary rather than repetitive

**Before**:
```java
AllureSteps.logStep(log, "Verify response status code is 200",
    () -> assertThat(response.getStatusCode())
        .as("Response status code should be 200")
        .isEqualTo(200));
```

**After**:
```java
AllureSteps.logStep(log, "Verify response status code is 200",
    () -> assertThat(response.getStatusCode())
        .as("Status code must be 200")
        .isEqualTo(EXPECTED_SUCCESS_STATUS));
```

---

## Implementation Sequence

### Phase 1: Foundation Changes (Low Priority First)
1. ✅ Add HTTP status code constants to `DeleteVideoGameBaseTest`
2. ✅ Replace hardcoded status codes in existing tests
3. ✅ Simplify assertion failure messages

### Phase 2: Core Fixes (Medium Priority)
4. ✅ Refactor `@AfterEach` → `@BeforeEach` cleanup pattern
5. ✅ Enhance response body assertions in existing tests

### Phase 3: New Coverage (High Priority)
6. ✅ Add `deleteNonExistentVideoGameTest()` with XSP-127
7. ✅ Add `deleteVideoGameWithInvalidIdTest()` with XSP-128
8. ✅ Add `deleteVideoGameWithoutAuthorizationTest()` with XSP-129

### Phase 4: Validation
9. ✅ Run all tests to ensure they pass
10. ✅ Verify no compilation errors
11. ✅ Check Allure report generation

---

## Files to Modify

| File | Changes |
|------|---------|
| `DeleteVideoGameBaseTest.java` | Add HTTP status code constants |
| `DeleteVideoGameComponentTest.java` | All fixes: refactor cleanup, enhance assertions, add negative tests |

---

## Assumptions & Questions

### Assumptions Made:
1. **Non-existent game deletion** should return HTTP 404
2. **Invalid ID format** should return HTTP 400
3. **Missing Authorization** should return HTTP 401 (pattern from GetVideoGameByIdComponentTest)
4. **Response structure** for delete operations only includes `status` field (verify with team)
5. **JIRA tickets** XSP-127, XSP-128, XSP-129 are available (or use actual ticket numbers)

### Questions for Team:
1. ✋ What HTTP status codes should be returned for error scenarios?
   - Non-existent game: 404, 400, or 204?
   - Invalid ID format: 400 or 404?
2. ✋ Does DeleteVideoGameResponseModel contain any other fields besides `status`?
3. ✋ What are the correct JIRA ticket numbers for negative test scenarios?
4. ✋ Should we test concurrent delete attempts (race condition testing)?
5. ✋ Any other edge cases to cover (permission-based deletion, soft delete vs hard delete)?

---

## Verification Checklist

- [ ] All existing tests still pass
- [ ] New negative tests added and passing
- [ ] No hardcoded HTTP status codes remain
- [ ] BeforeEach cleanup pattern applied
- [ ] Response body assertions enhanced
- [ ] Assertion messages simplified
- [ ] All tests have @TmsLink with correct tickets
- [ ] Allure report generation successful
- [ ] Code follows project conventions (verified via code review checklist)

---

## Rollback Plan

If issues arise:
1. Revert to previous version with `git revert`
2. Keep new test methods as separate branch for further refinement
3. Address failing tests one-by-one with team input

---

## Approval Request

**This plan requires user approval before implementation proceeds.**

Please review and confirm:
- ✅ Do you agree with the issue severity ratings?
- ✅ Do you prefer Option A (BeforeEach) for cleanup refactoring?
- ✅ Can you provide answers to the "Questions for Team" section?
- ✅ Are the assumed JIRA ticket numbers correct?
- ✅ Any additional requirements or edge cases not mentioned?

