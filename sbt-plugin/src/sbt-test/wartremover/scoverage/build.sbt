// TODO add 2.13.1 test
// https://github.com/scoverage/scalac-scoverage-plugin/pull/279
crossScalaVersions := Seq("2.13.0", "2.12.11")

coverageEnabled := true

wartremoverErrors += Wart.NonUnitStatements
