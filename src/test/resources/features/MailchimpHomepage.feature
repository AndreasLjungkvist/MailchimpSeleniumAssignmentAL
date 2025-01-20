Feature: MailchimpHomepage

Scenario Outline: CreateUser
  Given I can reach the webpage with my "<browser>".
  When I enter an email "<email>".
  When I enter a username "<username>".
  When I enter a password "<password>".
  When I choose to sign up.
  Then I can create a user"<create a user>".

  Examples:
    | browser | email      | username | password  | create a user                 |
    | Chrome  | erikolsson | Kurt     | Abcd/1234 | Successful                    |
    | Edge    | erikolsson | Long     | Abcd/1234 | Error: Long Username          |
    | Chrome  |            | Oskar    | Abcd/1234 | Error: No viable email        |
    | Edge    | erikolsson | Same     | Abcd/1234 | Error: Username already taken |
