# If your pull request fixes a bug

- Include regression tests for the bug.
- Confirm that the bugfix works with JRE7 and JRE8.
- Link to any relevant issues.

# If your pull request adds a wart

- Include positive and negative tests for the wart.
- Include a description of what the wart does.
- Indicate how the wart preserves correctness.
- Confirm that the code works with JRE7 and JRE8.
- Warts should be universal and limited to the language and standard library (see [wartremover-contrib](https://github.com/wartremover/wartremover-contrib) for warts that check libraries).
- Link to any relevant issues.

# If your pull request adds new functionality

- Indicate why this new functionality is useful.
- Confirm that the code works with JRE7 and JRE8.
- Link to any relevant issues.

Finally, don't forget to **specify `master` as target branch** (not `latest-release`)
