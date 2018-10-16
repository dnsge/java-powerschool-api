# java-powerschool-api
Interact with a Powerschool System and view user information

[![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](https://opensource.org/licenses/mit-license.php)

Example that lists `joey05`'s assignments in math, and their grades if applicable:
```java
PowerschoolClient client = new PowerschoolClient("https://url.of.powerschoolinstall");
User joey = client.authenticate("joey05", "joey's_password");

Course mathCourse = joey.newCourseGetter().containsByName("Math").first();

for (Assignment a : mathCourse.getAssignments(GradingPeriod.F1)) {

  System.out.println("Assignment name: " + a.getName());
  
  if (!a.isMissingDetails()) {
    System.out.println(" - Grade:  " + a.getScorePercent() + " (" + a.getScoreLetterGrade() + ")");
  }
  
}
```
### [Download Latest Version](https://github.com/dnsge/java-powerschool-api/latest)

#### Note
The requests aren't using an actual API, but rather parsing HTML from a webpage retrieved. Results could might vary, but shoudln't if your Powerschool System is a typical install.