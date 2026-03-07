---
name: jira-search
description: Helps user to search for Jira issues using filters and MCP server. Use when user want to interact with Jira issues.
---

# Jira Search

This skill allows you to search for Jira issues using Jira filters and the MCP server. Use this skill when you want to
interact with Jira issues, such as finding specific issues, checking their status, or retrieving details about them.

## Jira Filters

- Use JQL `filter = 'Automated tests'` filter to list all tests which are already automated (label: automated)
- Use JQL `filter = 'Tests to automate'` filter to list all tests which are not automated yet (label: to_automate)