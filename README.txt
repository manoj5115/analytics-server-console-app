UPSTOX Analytics 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Project structure -

1) src/main/java 				-> Contains all Java source files
2) src/main/resources	 		-> Contains application.properties
3) src/main/resources/data 		-> Contains provided trades.json file and added subscription.json file for providing input
4) src/test/java				-> Contains unit tests
5) build.gradle					-> Gradle tool to build and manage dependencies 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Please follow below instructions to run the application from terminal.

1) Clone the project from the provided Github link.
2) Navigate to project directory in your terminal.
3) Run on linux -> ./gradlew clean build
4) Run the Console app using below java cmd-
	java -jar ./build/libs/stox-analytics-console-app-0.0.1-SNAPSHOT.jar

Note: Please make sure to build the project again after you've modified the code and/or changed the input/symbol in subscription.json file. 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

1) This console application is written in Java and compiled/tested using Java 8. It uses Spring Boot and Gradle for building the code and manage dependencies.

2) AnalyticsEngine -> This class kicks off the process and starts the Analytics Engine. It relies on Spring framework to do some pre-requisite work of initializing the context for the Anlaytics Engine.

3) Context -> This class is primarily responsible for maintaining the context data for the Analytics Engine -
	a) Initializes Data pipeline for trade data processing
	b) Initializes configuration properties that will be used in different components of the engine.
	c) Maintains User subscription data to be used while trade processing.
	d) Maintains engine state to gracefully shut down the process after all trades are processed.
	
4) Trade file path can be configured in application.properties file and this will be takes care by Engine automatically during start-up.

5) Package name ending with suffix ".pojo" conatains all POJOs that are used by the Analytics Engine.

6) Package name ending with suffix ".tasks" contains tasks that are assigned to individual threads during trade processing.
	a) TradeDataReaderTask 	-> This is responsible for reading the Trade data input line by line and sends them to FSM thread.
	b) FsmTask				-> This is responsible for computeing OHLC packets based on 15 seconds (interval) and constructs 'BAR' chart data, based on timestamp TS2.
	c) SubscriptionTask		-> This is responsible for printing the BAR OHLC data on console as computed in real time. It maintains user subscription list through context.
	
7) Package name ending with suffix ".utils" contains constants and utility classes.
	a) AppUtils 	-> Contains utility methods for dealing with data/time logic, json data serde processsing, validating data pipeline state etc.
	b) FileReader	-> Contains method to generate stream from file data input like trades.json, subscription.json.
	

