#!/bin/bash

# Simple Budget Manager Application Runner
echo "Starting Budget Manager Application..."

# Run the application using Maven
mvn clean compile exec:java -Dexec.mainClass="com.budget.SimpleBudgetApp" -Dexec.args="--add-modules javafx.controls,javafx.fxml"
