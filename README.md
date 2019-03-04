# dbquerytool
Database query tool - Its a WEB tool that allow user to execute queries against Databases (right now - db2). Do not require local instalation.

Requirements: ✔ Java 8 ✔ Maven 2

Checkout, build and run

    Checkout as a Maven project
    After checkout you can go to the project folder and run:
    mvn clean This will clean any compiled code, garbage, etc
    mvn install - this will install dependencies
    mvn spring-boot:run This will build the project and launch it. You can access at: http://localhost:8080/queryToHtml

If you need to run in another port, use the syntax (where 8009 its some example): mvn spring-boot:run -Dserver.port=8009

To add your servers as default, edit the DbDAO class and the file query.html.

For support or questions, mail juliano.jmm@gmail.com
